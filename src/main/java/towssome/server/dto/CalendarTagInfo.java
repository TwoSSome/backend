package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "캘린더 태그 정보 DTO")
public record CalendarTagInfo(
        Long id,
        String name,
        int color,
        @Schema(description = "기본으로 생성된 태그라면 true, 커스텀 태그라면 false")
        boolean isDefaultTag
) {
}
