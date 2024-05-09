package towssome.server.dto;

public record CommentReq(
        String body,
        Long memberId,
        Long reviewPostId
) {

}
