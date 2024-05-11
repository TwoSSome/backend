package towssome.server.dto;

public record BookmarkReq(
        Long bookmarkId,
        Long reviewId,
        String summary,
        String photoPath
) {
}
