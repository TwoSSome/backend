package towssome.server.dto;

public record OAuthInitialConfigReq(
        String nickname,
        String jwt
) {
}
