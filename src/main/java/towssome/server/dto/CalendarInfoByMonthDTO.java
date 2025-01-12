package towssome.server.dto;

import towssome.server.entity.Member;

public record CalendarInfoByMonthDTO(
        Member member,
        int year,
        int month
) {
}
