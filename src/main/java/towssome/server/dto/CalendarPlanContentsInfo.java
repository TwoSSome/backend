package towssome.server.dto;

import java.time.LocalDate;

public record CalendarPlanContentsInfo(
        LocalDate planDate,
        String body
) {
}
