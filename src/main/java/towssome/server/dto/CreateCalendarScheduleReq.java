package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "캘린더 일정 생성 DTO")
public record CreateCalendarScheduleReq(
        @Schema(description = "일정을 포함시킬 태그 아이디")
        Long CalendarTagId,
        @Schema(description = "일정 제목")
        String title,
        @Schema(description = "시작 일자. yyyy-mm-dd 형식", example = "2025-01-09")
        LocalDate startDate,
        @Schema(description = "끝 일자. yyyy-mm-dd 형식", example = "2025-01-10")
        LocalDate endDate
) {
}
