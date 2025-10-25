package com.resumequill.app.modules.resumes.models;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ResumeModel {
  private int id;
  private int userId;
  private String data;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public ResumeModel(
    int id,
    int userId,
    String data,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
  ) {
    this.id = id;
    this.userId = userId;
    this.data = data;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
