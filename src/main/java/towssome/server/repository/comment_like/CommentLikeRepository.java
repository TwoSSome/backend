package towssome.server.repository.comment_like;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>,CommentLikeRepositoryCustom {
    CommentLike findByMemberIdAndCommentId(Long memberId, Long commentId);
}
