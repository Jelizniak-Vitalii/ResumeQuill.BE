package com.resumequill.app.modules.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserModel {
  private int id;
  private String email;

  @JsonIgnore
  private String password;

  private String firstName;
  private String phone;
  private String lastName;
  private String image;
  private String lang;
  private String googleId;
}
