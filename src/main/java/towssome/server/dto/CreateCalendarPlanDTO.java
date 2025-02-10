package towssome.server.dto;

import towssome.server.entity.CalendarSchedule;

import java.time.LocalDate;

public record CreateCalendarPlanDTO(
        String body,
        Long calendarScheduleId,
        LocalDate localDate
) {
}
