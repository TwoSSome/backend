package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "데이트 코스 응답")
public record DateCourseRes(
        @Schema(name = "데이트 코스의 고유 id")
        Long dateCourseId,
        int year,
        int month,
        int day,
        @Schema(name = "사진의 위도")
        Double longitude,
        @Schema(name = "사진의 경도")
        Double latitude,
        @Schema(name = "본문")
        String body,
        @Schema(name = "데이트 코스의 사진")
        String photoPath,
        @Schema(name = "작성자의 닉네임")
        String authorNickname,
        @Schema(name = "작성자의 프로필사진")
        String authorProfilePhoto
) {
}
