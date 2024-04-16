package towssome.server.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityPostRes(
    String title,
    String body,
    LocalDateTime createTime,
    LocalDateTime lastModifiedTime,
    List<PhotoInPost> photoPaths,
    Long reviewPostId
) {
}
