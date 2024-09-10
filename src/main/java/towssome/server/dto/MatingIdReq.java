package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatingIdReq(
        @Schema(description = "연인 신청 id")
        Long matingId
) {
}
