package towssome.server.dto;


import java.util.List;

public record ReviewSimpleRes(
        Long reviewId,
        String body,
        String profilePhoto,
        String nickname,
        String bodyPhoto,
        List<HashtagRes> hashTags
) {
}
