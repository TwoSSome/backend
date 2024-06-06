package towssome.server.dto;

import towssome.server.enumrated.ReviewType;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewPostRes(
        String body,
        int price,
        LocalDateTime createTime,
        LocalDateTime lastModifiedTime,
        Long memberId,
        List<PhotoInPost> photos,
        boolean isMyPost,
        boolean isLikedPost,
        boolean isBookmarked,
        List<String> hashTags,
        ReviewType reviewType,
        int startPoint,
        String whereBuy,
        String nickname
) {
}
