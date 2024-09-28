package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "데이트 코스 응답")
public record DateCourseRes(
        @Schema(description = "데이트 코스의 고유 id")
        Long dateCourseId,
        int year,
        int month,
        int day,
        @Schema(description = "사진의 위도")
        Double longitude,
        @Schema(description = "사진의 경도")
        Double latitude,
        @Schema(description = "본문")
        String body,
        @Schema(description = "데이트 코스의 사진")
        String photoPath,
        @Schema(description = "작성자의 닉네임")
        String authorNickname,
        @Schema(description = "작성자의 프로필사진")
        String authorProfilePhoto
) {
}
