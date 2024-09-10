package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import towssome.server.enumrated.MatingStatus;

@Schema(description = "연인 신청/요청 확인 DTO")
public record MatingRes(
        @Schema(description = "신청/요청 id")
        Long mateId,
        @Schema(description = "신청/요청 상태 확인, OFFER = 요청/신청 중, MATING = 연인 요청/신청 수락됨")
        MatingStatus status
) {
}
