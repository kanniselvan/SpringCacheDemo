package com.kanni.springCache.utils;

import com.kanni.springCache.exception.HomeApplicationError;
import com.kanni.springCache.exception.JWTException;
import com.kanni.springCache.model.JWTTokenInfo;
import com.kanni.springCache.service.impl.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtils.class);

    private static final String AUTHORITIES_KEY = "auth";

    @Autowired
    JWebToken jWebToken;


    public JWTTokenInfo getTokenInfo(String token) {
        return jWebToken.getTokenInfo(token);
    }


    @CacheEvict(cacheNames = "JWTTokenInfo", key = "#token")
    public void deleteJWTCache(String token) {

    }

    public Authentication getAuthentication(String token) {

        JWTTokenInfo jwtTokenInfo = jWebToken.getTokenInfo(token);


        Collection<? extends GrantedAuthority> authorities = jwtTokenInfo.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());

        User principal = new User(jwtTokenInfo.getUserName(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        jWebToken.getTokenInfo(token);

        if (!jWebToken.isValid())
            throw new HomeApplicationError("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);

        return true;
    }
}
