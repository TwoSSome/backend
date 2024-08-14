package towssome.server.oauth2;

public record UserDTO(
        String role,
        String nickname,
        String email,
        String username
) {
}
