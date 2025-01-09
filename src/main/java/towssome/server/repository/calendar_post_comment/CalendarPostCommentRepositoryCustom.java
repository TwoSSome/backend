package towssome.server.repository.calendar_post_comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.entity.CalendarPostComment;

public interface CalendarPostCommentRepositoryCustom {
    Page<CalendarPostComment> findCPCPageByCursorId(Long postId, Long cursorId, String sort, Pageable pageable);
}
