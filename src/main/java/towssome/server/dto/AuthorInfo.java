package towssome.server.dto;

public record AuthorInfo(
        Long id,
        String nickname,
        String profileImagePath
) {
}
