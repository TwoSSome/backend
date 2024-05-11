package towssome.server.dto;

import java.util.List;

public record ReviewPostListRes(
        Long reviewPostId,
        String body,
        List<PhotoInPost> photos,
        Long memberId,
        boolean isMyPost,
        boolean isBookmarked,
        boolean isLiked
) {
}
