package towssome.server.dto;

public record AccessRefreshRes(
        String access,
        String refresh,
        Long id
) {
}
