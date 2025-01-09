package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "캘린더 게시글 코멘트 생성 DTO")
public record CCPCReq(
        String body
) {
}
