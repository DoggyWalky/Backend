package com.doggyWalky.doggyWalky.report.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ChatReportRequestDto {

    private Long targetId;

    private Long jobPostId;

    private Long reporterId;
}
