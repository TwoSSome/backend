package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 캘린더 게시글에 종속될 코멘트
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarPostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_post_id")
    private CalendarPost calendarPost;

    public CalendarPostComment(String body, Member author, CalendarPost calendarPost) {
        this.body = body;
        this.author = author;
        this.calendarPost = calendarPost;
    }

    public void update(String body) {
        this.body = body;
    }
}
