package com.resumequill.app.modules.resumes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateResumeDto {
  @NotNull(message = "Resume data is required")
  private String data;
}
