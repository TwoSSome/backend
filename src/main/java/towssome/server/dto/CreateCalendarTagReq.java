package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 생성 요청")
public record CreateCalendarTagReq(
        @Schema(description = "생성할 이름", example = "맛집 탐방")
        String name,
        @Schema(description = "태그의 컬러 번호")
        int color
) {
}
