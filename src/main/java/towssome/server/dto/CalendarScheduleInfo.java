package towssome.server.dto;

import java.time.LocalDate;

public record CalendarScheduleInfo(
        CalendarTagInfo calendarTagInfo,
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        MemberInfo memberInfo
) {
}
