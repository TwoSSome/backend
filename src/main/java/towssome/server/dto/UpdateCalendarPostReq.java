package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "캘린더 게시글 업데이트 DTO")
public record UpdateCalendarPostReq(
        @Schema(description = "삭제할 사진 ID")
        List<Long> deletePhotoIdList,
        @Schema(description = "수정할 제목")
        String title,
        @Schema(description = "수정할 본문")
        String body
) {
}
