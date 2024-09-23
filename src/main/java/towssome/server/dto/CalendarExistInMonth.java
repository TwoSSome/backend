package towssome.server.dto;

import java.util.List;

public record CalendarExistInMonth(
        List<Integer> reviewExistDay,
        List<Integer> commentExistDay
) {
}
