package towssome.server.dto;

import java.util.List;

public record CalendarScheduleDetailInfo(
        String title,
        AuthorInfo authorInfo,
        List<PostInSchedule> postList
) {
}
