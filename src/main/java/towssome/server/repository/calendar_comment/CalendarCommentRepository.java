package towssome.server.repository.calendar_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarComment;

import java.util.List;

public interface CalendarCommentRepository extends JpaRepository<CalendarComment,Long>, CalendarCommentRepositoryCustom {

}
