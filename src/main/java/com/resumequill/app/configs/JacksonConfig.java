package com.resumequill.app.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.TimeZone;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
      .setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    for (HttpMessageConverter<?> c : converters) {
      if (c instanceof MappingJackson2HttpMessageConverter mj) {
        mj.setObjectMapper(objectMapper());
        return;
      }
    }
    converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
  }
}
