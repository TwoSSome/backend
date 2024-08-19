package towssome.server.dto;

public record JwtRes(
        String access,
        String refresh,
        Long id
) {
}
