package towssome.server.dto;

import java.time.LocalDateTime;

public record CommentListRes(
        Long commentId,
        String body,
        LocalDateTime createDate,
        LocalDateTime lastModifiedDate,
        Long memberId,
        Long reviewId
) {
}
