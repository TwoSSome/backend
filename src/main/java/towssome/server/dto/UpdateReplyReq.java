package towssome.server.dto;

public record UpdateReplyReq(
        Long id,
        String body,
        Long quotationId
) {
}
