package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "캘린더 일정 업데이트 DTO")
public record UpdateCalendarScheduleReq(
        @Schema(description = "수정할 제목")
        String title,
        @Schema(description = "수정할 시작 날짜", example = "2025-01-09")
        LocalDate startDate,
        @Schema(description = "수정할 끝 날짜", example = "2025-01-10")
        LocalDate endDate
) {
}
