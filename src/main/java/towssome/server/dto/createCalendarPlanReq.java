package towssome.server.dto;

import java.time.LocalDate;

public record createCalendarPlanReq(
        Long calendarScheduleId,
        String body,
        LocalDate localDate
) {
}
