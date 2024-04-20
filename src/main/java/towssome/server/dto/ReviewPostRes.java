package towssome.server.dto;

import towssome.server.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewPostRes(
        String body,
        int price,
        LocalDateTime createTime,
        LocalDateTime lastModifiedTime,
        Member member,
        List<PhotoInPost> photos
) {
}
