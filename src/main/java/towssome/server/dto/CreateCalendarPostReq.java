package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "캘린더 게시글 생성 DTO")
public record CreateCalendarPostReq(
        @Schema(description = "게시글을 생성할 일정의 아이디")
        Long calendarScheduleId,
        String title,
        String body
) {
}
