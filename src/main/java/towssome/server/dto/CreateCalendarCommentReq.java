package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "캘린더 코멘트 생성 DTO")
public record CreateCalendarCommentReq(
        @Schema(description = "본문")
        String body,
        @Schema(description = "연도")
        int year,
        @Schema(description = "월")
        int month,
        @Schema(description = "일")
        int day
) {
}
