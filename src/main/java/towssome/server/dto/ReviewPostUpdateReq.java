package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import towssome.server.enumrated.ReviewType;

import java.util.List;

@Schema(description = "리뷰글 수정 요청 dto")
public record ReviewPostUpdateReq(
        @Schema(description = "수정 본문")
        String body,
        @Schema(description = "수정 포인트")
        int price,
        @Schema(description = "삭제될 사진 id 리스트")
        List<Long> willDeletePhoto,
        @Schema(description = "수정 구매처")
        String whereBuy,
        @Schema(description = "수정 카테고리")
        String category,
        @Schema(description = "수정 리뷰 타입, GIVEN or RECEIVED")
        ReviewType reviewType,
        @Schema(description = "수정 품목")
        String item,
        @Schema(description = "수정 품목 url")
        String item_url
) {
}
