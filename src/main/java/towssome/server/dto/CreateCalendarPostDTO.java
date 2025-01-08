package towssome.server.dto;

import towssome.server.entity.Member;

public record CreateCalendarPostDTO(
        Member author,
        Long scheduleId,
        String title,
        String body
) {
}
