package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberNameReq(
        @Schema(description = "신청할 상대방의 아이디")
        String username
) {
}
