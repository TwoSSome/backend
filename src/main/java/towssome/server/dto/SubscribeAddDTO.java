package towssome.server.dto;

import towssome.server.entity.Member;

public record SubscribeAddDTO(
        Member subscriber,
        Member followed
) {
}
