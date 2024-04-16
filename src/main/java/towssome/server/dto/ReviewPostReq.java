package towssome.server.dto;

import towssome.server.entity.Member;

public record ReviewPostReq(
        String body,
        int price,
        Long memberId
) {
}
