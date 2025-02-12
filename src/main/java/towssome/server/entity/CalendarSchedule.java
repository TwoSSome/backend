package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 캘린더 태그에 종속될 일정
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate startDate; //시작 기간
    private LocalDate endDate; //끝 기간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_tag")
    private CalendarTag calendarTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    public CalendarSchedule(String name, LocalDate startDate, LocalDate endDate, CalendarTag calendarTag, Member author) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.calendarTag = calendarTag;
        this.author = author;
    }

    public void update(String title, LocalDate startDate, LocalDate endDate) {
        this.name = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
