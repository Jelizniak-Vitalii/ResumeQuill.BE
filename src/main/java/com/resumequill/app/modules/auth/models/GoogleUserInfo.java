package com.resumequill.app.modules.auth.models;

public record GoogleUserInfo(
  String googleId,
  String email,
  String firstName,
  String lastName,
  String picture
) {}
