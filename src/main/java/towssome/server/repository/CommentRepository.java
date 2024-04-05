package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
