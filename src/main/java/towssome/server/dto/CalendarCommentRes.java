package towssome.server.dto;

import java.time.LocalDateTime;

public record CalendarCommentRes(
        String body,
        Long id,
        LocalDateTime createdTime
) {
}
