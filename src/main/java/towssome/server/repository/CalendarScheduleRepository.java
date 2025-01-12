package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarSchedule;

import java.util.List;

public interface CalendarScheduleRepository extends JpaRepository<CalendarSchedule,Long> {

    @Query("SELECT s FROM CalendarSchedule s join CalendarTag ct on s.calendarTag.id = ct.id join Calendar c on c.id = ct.calendar.id" +
            " WHERE ((FUNCTION('MONTH', s.startDate) = :month AND FUNCTION('YEAR', s.startDate) = :year) " +
            "   OR (FUNCTION('MONTH', s.endDate) = :month AND FUNCTION('YEAR', s.endDate) = :year)) and c = :calendar" )
    List<CalendarSchedule> findByMonthAndYear(@Param("month") int month, @Param("year") int year, @Param("calendar")Calendar calendar);


}
