package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 성공 응답")
public record SocialLoginSuccessRes(
        @Schema(description = "AT")
        String access,
        @Schema(description = "RT")
        String refresh,
        @Schema(description = "member Id")
        Long id,
        @Schema(description = "아이디")
        String username,
        @Schema(description = "닉네임")
        String nickname
) {
}
