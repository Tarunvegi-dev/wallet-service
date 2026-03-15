package com.wallet.wallet_service.common.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter{
    
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if("/users/login".equals(request.getServletPath()) || "/users/signup".equals(request.getServletPath())){
            filterChain.doFilter(request, response);
            return;
        }

        String authHeaderToken = request.getHeader("Authorization");
        String prefix = "Bearer ";
        String userId = null;
        String token = null;

        if(authHeaderToken != null && authHeaderToken.startsWith(prefix)){
            token = authHeaderToken.replace(prefix, "");
            userId = jwtService.extractUserId(token);
        }

        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            if(jwtService.validateToken(token)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    
    
}
