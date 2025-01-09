package towssome.server.dto;

import towssome.server.entity.Member;

public record CCPCDTO(
        String body,
        Member author,
        Long postId
) {

}
