package towssome.server.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubscribeRes(

        String profilePhotoPath,
        String nickname,
        Long id,
        Long followingMemberId,
        LocalDateTime subscribeDate,
        List<HashtagRes> hashtagList

) {
}
