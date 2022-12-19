package com.kanni.springCache.filter;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kanni.springCache.exception.JWTException;
import com.kanni.springCache.model.JWTTokenInfo;
import com.kanni.springCache.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenValidateFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenValidateFilter.class);


    @Autowired
    private JWTUtils jwtUtils;


    private static final List<String> whiteListURL=new CopyOnWriteArrayList<>();



    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        whiteListURL.add("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        StopWatch timeMeasure = new StopWatch();
        timeMeasure.start();
        try {
            ThreadContext.put("requestId", RandomStringUtils.randomAlphanumeric(10));
            LOGGER.info("Started JwtTokenValidateFilter .........."+httpServletRequest.getMethod());

            String uri=httpServletRequest.getRequestURI();


            if(whiteListURL.stream().anyMatch(u->u.toLowerCase().contains(uri.toLowerCase()))){
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            String token=null;
            String bearerToken = httpServletRequest.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }else{
                throw new JWTException("JWT Token Not Found!!!");
            }

            LOGGER.info("token==="+token);

            if (StringUtils.isNotBlank(token) && jwtUtils.validateToken(token)) {
                Authentication authentication = jwtUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                JWTTokenInfo auth = jwtUtils.getTokenInfo(token);
                ThreadContext.put("userName", auth.getUserName());

                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                throw new JWTException("Something went wrong!!!!");
            }
        } catch (ExpiredJwtException eje) {
            LOGGER.error("ExpiredJwtException exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            LOGGER.debug("Exception " + eje.getMessage(), eje);
            return;
        } catch (JWTException ex) {
            LOGGER.error("JWTException exception {} ", ex.getMessage());
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return;

        } catch (Exception ex) {
            LOGGER.error("Exception :  {} ", ex.getMessage());
            httpServletResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            return;
        } finally {
            timeMeasure.stop();
            LOGGER.info("Last task time in second : {} ", timeMeasure.getTotalTimeSeconds());
            SecurityContextHolder.getContext().setAuthentication(null);
            ThreadContext.clearAll();
        }

    }


}
