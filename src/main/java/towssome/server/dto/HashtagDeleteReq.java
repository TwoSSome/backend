package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record HashtagDeleteReq(
        @Schema(description = "리뷰 id")
        Long reviewId,
        @Schema(description = "해시태그 id")
        Long hashtagId) {
}
