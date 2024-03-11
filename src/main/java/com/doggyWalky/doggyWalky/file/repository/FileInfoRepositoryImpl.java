package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.dto.schedule.DeletedFileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class FileInfoRepositoryImpl implements FileInfoRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<DeletedFileInfo> findDeletedFileInfo() {
        String sql = "SELECT fi.file_info_id AS id, f.file_id AS fileId " +
                "FROM file_info fi JOIN file f ON fi.file_id=f.file_id " +
                "WHERE fi.deleted_at <= DATE_SUB(CONVERT_TZ(now(),'+00:00', '+09:00'), INTERVAL 3 MONTH)";

        return jdbcTemplate.query(sql, new RowMapper<DeletedFileInfo>() {
            @Override
            public DeletedFileInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeletedFileInfo info = new DeletedFileInfo(rs.getLong("id"),rs.getLong("fileId"));
                return info;
            }
        });
    }
}
