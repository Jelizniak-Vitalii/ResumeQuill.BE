package com.resumequill.app.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class HttpLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    long start = System.currentTimeMillis();

    MDC.put("requestId", UUID.randomUUID().toString());
    MDC.put("userId", "null");
    MDC.put("method", request.getMethod());
    MDC.put("path", request.getRequestURI());

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.put("status", String.valueOf(response.getStatus()));
      MDC.put("durationMs", String.valueOf(System.currentTimeMillis() - start));
      MDC.clear();
    }
  }
}
