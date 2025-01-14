package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "캘린더 일정 DTO")
public record CalendarScheduleInfo(
        @Schema(description = "캘린더 태그 정보")
        CalendarTagInfo calendarTagInfo,
        @Schema(description = "캘린더 스케줄 아이디")
        Long id,
        @Schema(description = "캘린더 스케줄 제목")
        String title,
        @Schema(description = "캘린더 스케줄 시작 날짜")
        LocalDate startDate,
        @Schema(description = "캘린더 스케줄 끝 날짜")
        LocalDate endDate,
        @Schema(description = "캘린더 작성자 정보")
        AuthorInfo authorInfo
) {
}
