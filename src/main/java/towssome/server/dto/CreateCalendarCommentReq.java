package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "캘린더 코멘트 생성 DTO")
public record CreateCalendarCommentReq(
        @Schema(name = "본문")
        String body,
        @Schema(name = "연도")
        int year,
        @Schema(name = "월")
        int month,
        @Schema(name = "일")
        int day
) {
}
