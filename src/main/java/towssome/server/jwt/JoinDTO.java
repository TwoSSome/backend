package towssome.server.jwt;

public record JoinDTO(
        String username,
        String password,
        String nickname
) {
}
