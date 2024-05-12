package towssome.server.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubscribeRes(

        String profilePhotoPath,
        String nickname,
        Long subscribeId,
        Long followingMemberId,
        LocalDateTime subscribeDate

) {
}
