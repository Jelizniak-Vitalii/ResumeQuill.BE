package com.resumequill.app.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangeLangDto {
  @NotBlank(message = "Lang must not be empty")
  private String lang;
}
