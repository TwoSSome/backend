package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "가상 연인 프로필")
public record VirtualRes(
        @Schema(description = "가상 연인 이름")
        String name,
        @Schema(description = "가상 연인 해시태그")
        List<HashtagRes> hashtagRes,
        @Schema(description = "가상 연인 사진")
        String matePhotoPath
) {
}
