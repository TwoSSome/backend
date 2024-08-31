package towssome.server.dto;


import towssome.server.enumrated.ReviewType;

import java.util.List;

public record ReviewSimpleRes(
        Long reviewId,
        String body,
        String profilePhoto,
        String nickname,
        String bodyPhoto,
        ReviewType reviewType,
        List<HashtagRes> hashTags,
        Long likeAmount
) {
}
