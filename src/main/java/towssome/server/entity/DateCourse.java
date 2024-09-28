package towssome.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DateCourse {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 300)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    @Embedded
    private GPS gps;

    @Embedded
    private RegistrationDate date;

    //무조건 같이 조회되니까 EAGER
    @OneToOne
    private Photo photo;

    public DateCourse(String body, Member author, Calendar calendar, GPS gps, RegistrationDate date, Photo photo) {
        this.body = body;
        this.author = author;
        this.calendar = calendar;
        this.gps = gps;
        this.date = date;
        this.photo = photo;
    }

    public void update(String body) {
        this.body = body;
    }

}
