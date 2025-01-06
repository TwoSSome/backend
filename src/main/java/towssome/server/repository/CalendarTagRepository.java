package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarTag;

public interface CalendarTagRepository extends JpaRepository<CalendarTag,Long> {

}
