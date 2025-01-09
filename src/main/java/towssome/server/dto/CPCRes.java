package towssome.server.dto;

import towssome.server.entity.Member;

public record CPCRes(
        Long id,
        String body,
        ProfileSimpleRes commentedMember
) {
}
