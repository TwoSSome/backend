package towssome.server.dto;

import java.util.List;

public record CalendarScheduleDetailInfo(
        String title,
        Long authorId,
        List<PostInSchedule> postList
) {
}
