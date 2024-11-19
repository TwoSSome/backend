package towssome.server.dto;

import java.util.List;

public record ProfileRes_(
        String nickName,
        String profileImagePath,
        List<HashtagRes> profileTags,
        Long memberId
) {
}
