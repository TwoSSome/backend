package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰글 생성 요청 dto")
public record ReviewPostReq(
        @Schema(description = "본문")
        String body,
        @Schema(description = "요구 포인트")
        int price,
        @Schema(description = "구매처")
        String whereBuy,
        @Schema(description = "별점")
        int starPoint,
        @Schema(description = "카테고리")
        String category,
        @Schema(description = "리뷰 타입, 반드시 GIVEN or RECEIVED 둘 중 하나일 것")
        String reviewType
) {
}
