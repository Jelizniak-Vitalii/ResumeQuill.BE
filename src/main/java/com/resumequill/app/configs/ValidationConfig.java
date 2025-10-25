package com.resumequill.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@Configuration
@ControllerAdvice
public class ValidationConfig {
  private final Validator validator;

  public ValidationConfig(Validator validator) {
    this.validator = validator;
  }

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }


  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.setValidator(validator);
  }
}
