package towssome.server.dto;

import java.time.LocalDateTime;

public record CommentRes(
        Long commentId,
        String body,
        LocalDateTime createDate,
        LocalDateTime lastModifiedDate,
        Long reviewId,
        Boolean isLiked,
        Long likeCount,
        Boolean isfixed,
        ProfileSimpleRes commentedMember
) {
}
