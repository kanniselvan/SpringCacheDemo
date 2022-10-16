package com.kanni.springCache.filter;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kanni.springCache.exception.HomeApplicationError;
import com.kanni.springCache.exception.JWTException;
import com.kanni.springCache.model.JWTTokenInfo;
import com.kanni.springCache.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenValidateFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenValidateFilter.class);


        @Autowired
        private JWTUtils jwtUtils;

        public JwtTokenValidateFilter() { }

        @Override
        protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

            try {
                String token = jwtUtils.resolveToken(httpServletRequest);
                 if (StringUtils.isBlank(token)) {
                   throw new JWTException("JWT Token Not Found!!!");
                }
                if (StringUtils.isNotBlank(token) && jwtUtils.validateToken(token)) {
                        Authentication authentication = jwtUtils.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        JWTTokenInfo auth = jwtUtils.getTokenInfo(token);
                        System.out.println(auth);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                }else{
                    throw new JWTException("Something went wrong!!!!");
                }
            }catch (ExpiredJwtException eje) {
                LOGGER.error("ExpiredJwtException exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                LOGGER.debug("Exception " + eje.getMessage(), eje);
                return;
            } catch (JWTException ex) {
                LOGGER.error("JWTException exception {} ",ex.getMessage());
                SecurityContextHolder.clearContext();
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                return;

            }catch (Exception ex){
                LOGGER.error("Exception :  {} ",ex.getMessage());
                httpServletResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(null);
        }



    }
