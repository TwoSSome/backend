package towssome.server.repository.calendar_post_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPostComment;

public interface CalendarPostCommentRepository extends JpaRepository<CalendarPostComment,Long>, CalendarPostCommentRepositoryCustom {
}
