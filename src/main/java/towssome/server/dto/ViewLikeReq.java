package towssome.server.dto;

public record ViewLikeReq(
    Long reviewId,
    Long memberId
) {
}
