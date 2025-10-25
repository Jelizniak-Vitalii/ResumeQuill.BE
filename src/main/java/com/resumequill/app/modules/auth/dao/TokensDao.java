package com.resumequill.app.modules.auth.dao;

import com.resumequill.app.modules.auth.models.RefreshToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TokensDao {
  private final JdbcTemplate jdbcTemplate;

  public TokensDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<RefreshToken> mapper = (rs, i) -> new RefreshToken(
    rs.getInt("id"),
    rs.getInt("user_id"),
    rs.getString("token"),
    rs.getString("user_agent"),
    rs.getString("ip"),
    rs.getTimestamp("expires_at").toInstant()
  );

  public RefreshToken create(RefreshToken refreshToken) {
    String sql = "INSERT INTO sessions (user_id, token, user_agent, ip, expires_at) VALUES (?, ?, ?, ?, ?) RETURNING *";
    return jdbcTemplate.queryForObject(
      sql,
      mapper,
      refreshToken.getUserId(),
      UUID.fromString(refreshToken.getToken()),
      refreshToken.getUserAgent(),
      refreshToken.getIp(),
      Timestamp.from(refreshToken.getExpiresAt())
    );
  }

  public RefreshToken findByUserId(int userId) {
    String sql = "SELECT * FROM sessions WHERE user_id = ?";
    return jdbcTemplate.queryForObject(sql, mapper, userId);
  }

  public Optional<RefreshToken> findByToken(String token) {
    String sql = "SELECT * FROM sessions WHERE token = ? LIMIT 1 FOR UPDATE NOWAIT";
    return jdbcTemplate.query(sql, mapper, token).stream().findFirst();
  }

  public int deleteById(int id) {
    String sql = "DELETE FROM sessions WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  public int deleteByToken(String token) {
    String sql = "DELETE FROM sessions WHERE token = ?";
    return jdbcTemplate.update(sql, token);
  }

  public int deleteByUserId(int user_id) {
    String sql = "DELETE FROM sessions WHERE user_id = ?";
    return jdbcTemplate.update(sql, user_id);
  }

  public int deleteByUserAgentAndIp(int userId, String userAgent, String ip) {
    String sql = "DELETE FROM sessions WHERE user_id = ? AND user_agent = ? AND ip = ?";
    return jdbcTemplate.update(sql, userId, userAgent, ip);
  }
}
