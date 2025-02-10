package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Lob
    String body;

    @ManyToOne
    @JoinColumn(name = "calendar_schedule_id")
    CalendarSchedule calendarSchedule;

    LocalDate planDate;

    public CalendarPlan(String body, CalendarSchedule calendarSchedule, LocalDate planDate) {
        this.body = body;
        this.calendarSchedule = calendarSchedule;
        this.planDate = planDate;
    }

    public void update(String body) {
        this.body = body;
    }
}
