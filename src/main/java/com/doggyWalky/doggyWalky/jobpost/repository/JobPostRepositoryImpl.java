package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.file.dto.schedule.DeletedFileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class JobPostRepositoryImpl implements JobPostRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> findDeletedJobPost() {
        String sql = "SELECT jp.jobpost_id AS id " +
                "FROM job_post jp " +
                "WHERE jp.deleted_yn = true AND jp.updated_at <= DATE_SUB(CONVERT_TZ(now(),'+00:00', '+09:00'), INTERVAL 3 MONTH)";

        return jdbcTemplate.query(sql, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long jobPostId = rs.getLong("id");
                return jobPostId;
            }
        });
    }

    @Override
    public void batchDeleteJobPost(List<Long> jobPostList) {
        String sql = "DELETE FROM job_post WHERE jobpost_id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long jobPostId = jobPostList.get(i);
                ps.setLong(1, jobPostId);
            }

            @Override
            public int getBatchSize() {
                return jobPostList.size();
            }
        });
    }


}
