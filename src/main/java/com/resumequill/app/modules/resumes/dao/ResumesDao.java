package com.resumequill.app.modules.resumes.dao;

import com.resumequill.app.modules.resumes.models.ResumeModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ResumesDao {
  private final JdbcTemplate jdbcTemplate;

  public ResumesDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<ResumeModel> mapper = (rs, i) -> new ResumeModel(
    rs.getInt("id"),
    rs.getInt("user_id"),
    rs.getString("data"),
    rs.getObject("created_at", OffsetDateTime.class),
    rs.getObject("updated_at", OffsetDateTime.class)
  );

  public List<ResumeModel> findAllByUser(int userId) {
    String sql = "SELECT id, user_id, data, created_at, updated_at " +
      "FROM resumes WHERE user_id = ? ORDER BY created_at DESC";
    return jdbcTemplate.query(sql, mapper, userId);
  }

  public Optional<ResumeModel> findById(int userId, int resumeId) {
    String sql = "SELECT id, user_id, data, created_at, updated_at FROM resumes WHERE id = ? AND user_id = ?";
    return jdbcTemplate.query(sql, mapper, resumeId, userId).stream().findFirst();
  }

  public ResumeModel create(int userId, String data) {
    String sql = "INSERT INTO resumes (user_id, data) VALUES (?, CAST(? AS JSONB)) RETURNING *";
    return jdbcTemplate.queryForObject(sql, mapper, userId, data);
  }

  public int updateData(int userId, int resumeId, String data) {
    String sql = "UPDATE resumes SET data = CAST(? AS JSONB) WHERE id = ? AND user_id = ?";
    return jdbcTemplate.update(sql, data, resumeId, userId);
  }

  public int delete(int userId, int resumeId) {
    return jdbcTemplate.update("DELETE FROM resumes WHERE id = ? and user_id = ?", resumeId, userId);
  }
}
