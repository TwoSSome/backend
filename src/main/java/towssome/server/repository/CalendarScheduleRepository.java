package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarSchedule;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule,Long> {
}
