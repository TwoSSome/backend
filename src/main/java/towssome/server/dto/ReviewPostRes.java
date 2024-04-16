package towssome.server.dto;

import towssome.server.entity.Member;

public record ReviewPostRes(
        String body,
        int price,
        Member member
) {
}
