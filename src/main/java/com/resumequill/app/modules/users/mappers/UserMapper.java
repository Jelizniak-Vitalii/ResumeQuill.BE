package com.resumequill.app.modules.users.mappers;

import com.resumequill.app.modules.users.models.UserModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<UserModel> {
  @Override
  public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserModel user = new UserModel();

    user.setId(rs.getInt("id"));
    user.setEmail(rs.getString("email"));
    user.setPhone(rs.getString("phone"));
    user.setPassword(rs.getString("password"));
    user.setFirstName(rs.getString("first_name"));
    user.setLastName(rs.getString("last_name"));
    user.setImage(rs.getString("image"));
    user.setLang(rs.getString("lang"));

    return user;
  }
}
