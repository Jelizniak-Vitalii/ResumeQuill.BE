package com.resumequill.app.configs;

import com.resumequill.app.common.filters.HttpLoggingFilter;
import jakarta.servlet.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.EnumSet;

@Configuration
public class ServletFilterConfig {

  public static void register(ServletContext servletContext) {
    registerHttpLoggingFilter(servletContext);
    registerEncodingFilter(servletContext);
    // дальше: security, cors, metrics
  }

  private static void registerHttpLoggingFilter(ServletContext servletContext) {
    FilterRegistration.Dynamic filter =
      servletContext.addFilter("httpLoggingFilter", new HttpLoggingFilter());

    filter.addMappingForUrlPatterns(
      EnumSet.of(DispatcherType.REQUEST),
      false,
      "/*"
    );

    filter.setAsyncSupported(true);
  }

  private static void registerEncodingFilter(ServletContext servletContext) {
    CharacterEncodingFilter encoding = new CharacterEncodingFilter();
    encoding.setEncoding("UTF-8");
    encoding.setForceEncoding(true);

    servletContext
      .addFilter("encodingFilter", encoding)
      .addMappingForUrlPatterns(null, false, "/*");
  }
}
