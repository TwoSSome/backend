package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommentLike;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>,CommentLikeRepositoryCustom {
    CommentLike findByMemberIdAndCommentId(Long memberId, Long commentId);
}
