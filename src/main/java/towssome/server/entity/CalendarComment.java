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

    @Embedded
    private RegistrationDate date;

    public CalendarComment(Member author, String body, Calendar calendar, RegistrationDate date) {
        this.author = author;
        this.body = body;
        this.calendar = calendar;
        this.date = date;
    }

    public void update(String body) {
        this.body = body;
    }

}
