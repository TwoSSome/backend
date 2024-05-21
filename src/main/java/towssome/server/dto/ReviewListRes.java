package towssome.server.dto;

import java.util.List;

public record ReviewListRes(
        String body,
        int price,
        Long memberId,
        List<PhotoInPost> photos,
        List<String> hashTags){
}
