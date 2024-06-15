package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FollowReq(
        @Schema(description = "구독할 멤버의 id")
        Long subscribeId
) {
}
