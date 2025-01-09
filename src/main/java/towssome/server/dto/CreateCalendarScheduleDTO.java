package towssome.server.dto;

import towssome.server.entity.Member;

import java.time.LocalDate;

public record CreateCalendarScheduleDTO(
        Long CalendarTagId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Member member
) {
}
