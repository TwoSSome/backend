package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 프로필 초기 설정")
public record OAuthInitialConfigReq(

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "ID")
        String username,

        @Schema(description = "발급받은 jwt")
        String jwt
) {
}
