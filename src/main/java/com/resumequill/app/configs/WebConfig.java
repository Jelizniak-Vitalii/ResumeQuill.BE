package com.resumequill.app.configs;

import com.resumequill.app.modules.auth.interceptors.JwtInterceptor;
//import com.resumequill.app.auth.interceptors.RoleInterceptor;
import com.resumequill.app.common.annotations.NoApiPrefix;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
  @Value("${files.path}")
  private String storagePath;

  private final JwtInterceptor jwtInterceptor;

  public WebConfig(
    JwtInterceptor jwtInterceptor
  ) {
    this.jwtInterceptor = jwtInterceptor;
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix("/api/v2", clazz ->
      !clazz.isAnnotationPresent(NoApiPrefix.class)
    );
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowedHeaders("*")
      .allowCredentials(true);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
      .addPathPatterns("/api/v2/**")
      .excludePathPatterns("/public/**");

//    registry.addInterceptor(roleInterceptor)
//      .addPathPatterns("/api/**");
  }

//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    registry.addResourceHandler("/uploads/**")
//      .addResourceLocations("file:" + storagePath)  // Adjust this path based on your storagePath
//      .setCachePeriod(0);  // No caching, or adjust as needed
//  }
}
