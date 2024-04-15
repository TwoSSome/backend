package towssome.server.dto;

import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;

public record CommentDTO(
        String body,
        Member member,
        ReviewPost reviewPost
) {

}
