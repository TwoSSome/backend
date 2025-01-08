package towssome.server.dto;

import towssome.server.entity.Member;

public record CreateCalendarTagDTO(
        Member member,
        String name,
        int color
) {
}
