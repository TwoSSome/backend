package towssome.server.dto;

public record AddBookmarkReq(
        Long categoryId,
        Long reviewPostId
) {
}
