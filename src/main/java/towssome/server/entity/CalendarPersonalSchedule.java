package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 일정 (남자친구/여자친구)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarPersonalSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int color; // 컬러 번호

    @ManyToOne
    private Calendar calendar;

    public CalendarPersonalSchedule(String name, int color, Calendar calendar) {
        this.name = name;
        this.color = color;
        this.calendar = calendar;
    }
}
