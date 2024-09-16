package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "소셜 프로필 초기 설정")
public record OAuthInitialConfigReq(

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "ID")
        String username,

        @Schema(description = "발급받은 jwt")
        String jwt,

        @Schema(description = "프로필 태그")
        List<String> profileTags
) {
}
