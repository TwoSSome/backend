package towssome.server.dto;

import java.time.LocalDate;

public record UpdateCalendarScheduleDTO(
        Long CalendarScheduleId,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
}
