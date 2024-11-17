package towssome.server.dto;

public record ProfileSimpleRes(
        String nickName,
        String profileImagePath,
        Long memberId
) {
}
