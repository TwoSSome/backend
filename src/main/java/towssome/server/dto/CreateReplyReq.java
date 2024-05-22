package towssome.server.dto;

public record CreateReplyReq(
        String body,
        boolean isAnonymous,
        Long quotationId,
        Long postId
) {
}
