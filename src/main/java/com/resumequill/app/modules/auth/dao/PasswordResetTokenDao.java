package com.resumequill.app.modules.auth.dao;

import com.resumequill.app.modules.auth.models.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenDao {
  private final JdbcTemplate jdbcTemplate;

  public PasswordResetTokenDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void create(PasswordResetToken token) {
    String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?::uuid, ?)";
    jdbcTemplate.update(sql, token.getUserId(), token.getToken(), Timestamp.from(token.getExpiresAt()));
  }

  public Optional<PasswordResetToken> findByToken(String token) {
    String sql = "SELECT * FROM password_reset_tokens WHERE token = ?::uuid";
    List<PasswordResetToken> tokens = jdbcTemplate.query(sql, new PasswordResetTokenMapper(), token);
    return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.getFirst());
  }

  public void deleteByToken(String token) {
    String sql = "DELETE FROM password_reset_tokens WHERE token = ?::uuid";
    jdbcTemplate.update(sql, token);
  }

  public void deleteByUserId(int userId) {
    String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
    jdbcTemplate.update(sql, userId);
  }

  public void deleteExpired() {
    String sql = "DELETE FROM password_reset_tokens WHERE expires_at < now()";
    jdbcTemplate.update(sql);
  }

  private static class PasswordResetTokenMapper implements RowMapper<PasswordResetToken> {
    @Override
    public PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
      PasswordResetToken token = new PasswordResetToken();
      token.setId(rs.getInt("id"));
      token.setUserId(rs.getInt("user_id"));
      token.setToken(rs.getString("token"));
      token.setCreatedAt(rs.getTimestamp("created_at").toInstant());
      token.setExpiresAt(rs.getTimestamp("expires_at").toInstant());
      return token;
    }
  }
}
