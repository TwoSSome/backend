package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPlan;
import towssome.server.entity.CalendarSchedule;

import java.util.List;

public interface CalendarPlanRepository extends JpaRepository<CalendarPlan, Long> {

    List<CalendarPlan> getCalendarPlanByCalendarSchedule(CalendarSchedule calendarSchedule);

}
