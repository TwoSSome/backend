package towssome.server.dto;

public record MemberInfo(
        Long id,
        String nickname,
        String profileImagePath
) {
}
