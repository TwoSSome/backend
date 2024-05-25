package towssome.server.repository;

public interface CommentLikeRepositoryCustom {
    Long countByCommentId(Long commentId);
}
