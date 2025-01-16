package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.enumrated.PostType;

/**
 * 캘린더 일정에 종속될 게시글
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarPost extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Lob
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @ManyToOne(fetch = FetchType.EAGER)
    private CalendarSchedule calendarSchedule;

    @Enumerated(value = EnumType.STRING)
    private PostType postType;

    public CalendarPost(String title, String body, Member author, CalendarSchedule calendarSchedule, PostType postType) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.calendarSchedule = calendarSchedule;
        this.postType = postType;
    }

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
