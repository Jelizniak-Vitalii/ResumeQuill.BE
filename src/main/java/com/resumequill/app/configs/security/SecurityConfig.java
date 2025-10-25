package com.resumequill.app.configs.security;

import com.resumequill.app.modules.auth.filters.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Bean
//  SecurityFilterChain api(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
//    http.csrf(csrf -> csrf.disable());
//    http.cors(Customizer.withDefaults());
//
//    http.authorizeHttpRequests(auth -> auth
//      .requestMatchers("/api/auth/**", "/public/**", "/health", "/actuator/**", "/docs/**").permitAll()
//      .anyRequest().authenticated()
//    );
//
//    // 401 для неаутентифицированных
//    http.exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
//      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//      res.setContentType("application/json");
//      res.getWriter().write("{\"error\":\"Unauthorized\"}");
//    }));
//
//    http.addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
//    return http.build();
//  }
//
//  // Опционально CORS (если нужно явно)
//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    var cfg = new CorsConfiguration();
//    cfg.setAllowedOrigins(List.of("*"));
//    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//    cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
//    var src = new UrlBasedCorsConfigurationSource();
//    src.registerCorsConfiguration("/**", cfg);
//    return src;
//  }
}
