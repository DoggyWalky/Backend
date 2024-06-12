package com.doggyWalky.doggyWalky.report.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ChatReportRequestDto {

    private Long targetId;

    private Long jobPostId;

    private Long reporterId;
}
