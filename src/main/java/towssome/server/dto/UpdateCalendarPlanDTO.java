package towssome.server.dto;

import java.time.LocalDate;

public record UpdateCalendarPlanDTO(
        Long id,
        String body,
        LocalDate localDate
) {
}
