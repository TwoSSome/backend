package towssome.server.dto;

import towssome.server.entity.Member;

public record ReviewPostDTO(
        String body,
        int price,
        Member member,
        String photoPath
) {
}
