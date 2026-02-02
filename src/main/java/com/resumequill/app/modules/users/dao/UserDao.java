package com.resumequill.app.modules.users.dao;

import com.resumequill.app.modules.users.mappers.UserMapper;
import com.resumequill.app.modules.users.models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
  private final JdbcTemplate jdbcTemplate;

  public UserDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<UserModel> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.query(sql, new UserMapper());
  }

  public UserModel findById(int id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
  }

  public UserModel findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";

    List<UserModel> users = jdbcTemplate.query(sql, new UserMapper(), email);

    return users.isEmpty() ? null : users.getFirst();
  }

  public Integer createUser(UserModel user) {
    String sql = "INSERT INTO users (email, password, phone, first_name, last_name) VALUES (?, ?, ?, ?, ?) RETURNING id";

    return jdbcTemplate.queryForObject(sql, Integer.class, user.getEmail(), user.getPassword(), user.getPhone(),
      user.getFirstName(), user.getLastName());
  }

  public void updateLang(int userId, String lang) {
    String sql = "UPDATE users SET lang = ? WHERE id = ?";
    jdbcTemplate.update(sql, lang, userId);
  }

  public void updateUserImage(int userId, String filePath) {
    String sql = "UPDATE users SET image = ? WHERE id = ?";
    jdbcTemplate.update(sql, filePath, userId);
  }

  public void update(UserModel user) {
    String sql = "UPDATE users SET email = ?, phone = ?, first_name = ?, last_name = ? WHERE id = ?";

    jdbcTemplate.update(sql, user.getEmail(), user.getPhone(), user.getFirstName(), user.getLastName(), user.getId());
  }

  public UserModel findByGoogleId(String googleId) {
    String sql = "SELECT * FROM users WHERE google_id = ?";
    List<UserModel> users = jdbcTemplate.query(sql, new UserMapper(), googleId);
    return users.isEmpty() ? null : users.getFirst();
  }

  public Integer createOAuthUser(UserModel user) {
    String sql = "INSERT INTO users (email, first_name, last_name, google_id, image) VALUES (?, ?, ?, ?, ?) RETURNING id";
    return jdbcTemplate.queryForObject(sql, Integer.class,
      user.getEmail(), user.getFirstName(), user.getLastName(), user.getGoogleId(), user.getImage());
  }

  public void linkGoogleAccount(int userId, String googleId) {
    String sql = "UPDATE users SET google_id = ? WHERE id = ?";
    jdbcTemplate.update(sql, googleId, userId);
  }
}
