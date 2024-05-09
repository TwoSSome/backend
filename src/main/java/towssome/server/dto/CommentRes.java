package towssome.server.dto;

import java.time.LocalDateTime;

public record CommentRes(
        Long id,
        String body,
        LocalDateTime createTime,
        LocalDateTime lastModifiedTime,
        Long memberId,
        Long reviewPostId
) {
}
