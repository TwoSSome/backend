package towssome.server.dto;

public record CommentReq(
        String body,
        Long reviewPostId
) {

}
