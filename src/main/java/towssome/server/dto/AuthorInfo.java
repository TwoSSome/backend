package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작성자 정보")
public record AuthorInfo(
        @Schema(description = "작성자 아이디")
        Long id,
        @Schema(description = "작성자 닉네임")
        String nickname,
        @Schema(description = "작성자 프로필사진")
        String profileImagePath
) {
}
