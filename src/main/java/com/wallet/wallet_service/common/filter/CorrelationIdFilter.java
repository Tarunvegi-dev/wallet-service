package com.wallet.wallet_service.common.filter;

import java.io.IOException;
import java.util.UUID;

import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader("X-Correlation-Id");
        if(!StringUtils.hasText(correlationId)){
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);
        try{
            filterChain.doFilter(request, response);
        }finally{
            MDC.remove("correlationId");
        }
    }
    
}
