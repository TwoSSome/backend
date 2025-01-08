package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "생성 ID 반환 DTO")
public record CreateRes(
        @Schema(description = "생성된 객체의 ID")
        Long createdId
) {
}
