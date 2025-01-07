package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarTag;

import java.util.List;

public interface CalendarTagRepository extends JpaRepository<CalendarTag,Long> {

    List<CalendarTag> findAllByCalendar(Calendar calendar);

}
