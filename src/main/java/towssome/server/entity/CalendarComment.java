package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarComment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(length = 200)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    public CalendarComment(Member author, String body, Calendar calendar) {
        this.author = author;
        this.body = body;
        this.calendar = calendar;
    }



}
