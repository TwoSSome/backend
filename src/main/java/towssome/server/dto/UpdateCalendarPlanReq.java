package towssome.server.dto;

import java.time.LocalDate;

public record UpdateCalendarPlanReq(
        LocalDate localDate,
        String body
) {
}
