package towssome.server.dto;

import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;

public record CommunityPostSaveDTO(
        String title,
        String body,
        ReviewPost reviewPost,
        Member member,
        boolean isAnonymous
) {
}
