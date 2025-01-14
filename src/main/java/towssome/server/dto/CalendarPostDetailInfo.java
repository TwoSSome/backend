package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "캘린더 포스트 DTO")
public record CalendarPostDetailInfo(
        @Schema(description = "게시글 사진 URI와 ID")
        List<PhotoInPost> photoPaths,
        @Schema(description = "게시글 제목")
        String title,
        @Schema(description = "게시글 본문")
        String body,
        @Schema(description = "작성자 ID")
        AuthorInfo authorInfo
) {
}
