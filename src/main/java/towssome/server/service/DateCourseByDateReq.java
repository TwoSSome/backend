package towssome.server.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DateCourseByDateReq(
        int startYear,
        int endYear,
        int startMonth,
        int endMonth,
        int startDay,
        int endDay
) {
}
