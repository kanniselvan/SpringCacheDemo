package com.kanni.springCache.utils;

import com.kanni.springCache.exception.HomeApplicationError;
import com.kanni.springCache.exception.JWTException;
import com.kanni.springCache.model.JWTTokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

    private static final Logger LOGGER= LoggerFactory.getLogger(JWTUtils.class);

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    private static final String AUTHORITIES_KEY = "auth";


    @Cacheable(cacheNames = "JWTTokenInfo",key = "#token")
    public JWTTokenInfo getTokenInfo(String token) {
        LOGGER.info("Create entry in Cache.....");
        if (StringUtils.isNotBlank(token)) {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (ObjectUtils.isNotEmpty(claims) && ObjectUtils.isNotEmpty(claims.getBody())) {
                String userName = getUserName(claims.getBody());
                String issuer = claims.getBody().getIssuer();
                String role = claims.getBody().get("Role").toString();
                String email = String.valueOf(claims.getBody().get("Email"));
                Date expiry = claims.getBody().getExpiration();
                JWTTokenInfo jwtTokenInfo = new JWTTokenInfo();
                jwtTokenInfo.setUserName(userName);
                jwtTokenInfo.setIssuer(issuer);
                jwtTokenInfo.setExpiryDate(expiry);
                jwtTokenInfo.setRole(role);
                jwtTokenInfo.setEmail(email);
                return jwtTokenInfo;
            }
        }
        throw new JWTException("Token should not be null!!!!");
    }


    @CacheEvict(cacheNames = "JWTTokenInfo", key = "#token")
    public void deleteJWTCache(String token) {

    }

    public String getUserName(Claims claims) {
        if(Optional.ofNullable(claims).isPresent()) {
            String userName = String.valueOf(claims.getSubject());
            if (StringUtils.isBlank(userName) || StringUtils.equalsIgnoreCase(userName,"null")) {
                userName= String.valueOf(claims.get("username"));
            }
            return userName;
        }
        return null;
    }
    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")).stream()
                .map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());

        User principal = new User(getUserName(claims), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public JWTTokenInfo getJWTTokenInfo(HttpServletRequest req){
        String token=resolveToken(req);
        if(StringUtils.isNotBlank(token)){
            return getTokenInfo(token);
        }
        return null;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new HomeApplicationError("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
