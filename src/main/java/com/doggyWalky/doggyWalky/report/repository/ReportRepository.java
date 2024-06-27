package com.doggyWalky.doggyWalky.report.repository;

import com.doggyWalky.doggyWalky.chat.dto.response.ChatMessageResponse;
import com.doggyWalky.doggyWalky.report.entity.Report;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    @Query(value = "select new com.doggyWalky.doggyWalky.chat.dto.response.ChatMessageResponse(c.id,c.member.id,c.createdAt,c.content,c.readYn,c.deleteYn) from Chat c " +
            "where c.chatRoom.id = (select cr.id from ChatRoom cr where cr.id IN (select crms.chatRoom.id from ChatRoomMembership crms where crms.member.id = :reporterId " +
            "and crms.opponent.id = :targetId ) and cr.jobPostId = :jobPostId)")
    List<ChatMessageResponse> getChatMessagesForReport(@Param("reporterId") Long reporterId, @Param("targetId") Long targetId, @Param("jobPostId") Long jobPostId);

    @Modifying
    @Query("delete from Report r where r.jobPost.id = :jobPostId")
    void deleteByJobPostId(@Param("jobPostId") Long jobPostId);
}
