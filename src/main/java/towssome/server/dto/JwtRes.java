package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "jwt 응답 객체")
public record JwtRes(
        String jwt
) {
}
