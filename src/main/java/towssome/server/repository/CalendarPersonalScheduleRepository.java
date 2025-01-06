package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPersonalSchedule;

public interface CalendarPersonalScheduleRepository extends JpaRepository<CalendarPersonalSchedule,Long> {
}
