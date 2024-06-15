package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CancelFollowReq(
        @Schema(description = "취소할 구독 id (계정 id가 아님!!)")
        Long cancelId
) {
}
