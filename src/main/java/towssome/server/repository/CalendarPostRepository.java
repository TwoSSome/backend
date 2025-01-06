package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPost;

public interface CalendarPostRepository extends JpaRepository<CalendarPost,Long> {
}
