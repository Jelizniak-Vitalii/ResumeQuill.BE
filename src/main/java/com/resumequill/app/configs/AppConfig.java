package com.resumequill.app.configs;

import com.resumequill.app.configs.db.DataBaseConfig;
import com.resumequill.app.configs.db.FlywayConfig;
import com.resumequill.app.configs.security.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@ComponentScan(basePackages = "com.resumequill.app")
@Import({
  WebConfig.class,
  DataBaseConfig.class,
  FlywayConfig.class,
	SecurityConfig.class,
  ValidationConfig.class,
  JacksonConfig.class,
  MetricsConfig.class
})
public class AppConfig {
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
//		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//		Resource resource = new ClassPathResource("application.properties");
//		configurer.setLocation(resource);
//
//		return configurer;
//	}

	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
}
