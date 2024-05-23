package towssome.server.dto;

public record ReplyRes(
        Long id,
        String body,
        boolean isAnonymous,
        boolean isMyPost,
        boolean isAdoption,
        Long quotationId,
        Long memberId,
        String memberNickname
) {
}
