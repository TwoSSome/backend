package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "calendar_comment")
public class CalendarComment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_member_id")
    private Member author;

    @Column(length = 200)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    private int year;

    private int month;

    private int day;

    public CalendarComment(Member author, String body, Calendar calendar, int year, int month, int day) {
        this.author = author;
        this.body = body;
        this.calendar = calendar;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void update(String body) {
        this.body = body;
    }

}
