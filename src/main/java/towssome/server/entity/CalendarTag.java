package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.dto.CalendarTagInfo;

/**
 * 캘린더에 종속되는 태그
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CalendarTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //태그 이름

    private int color; //컬러 번호

    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendar;

    private Boolean isDefaultTag;
    private Boolean isPersonalTag;

    public CalendarTag(String name, int color, Calendar calendar, Boolean isDefaultTag, Boolean isPersonalTag) {
        this.name = name;
        this.color = color;
        this.calendar = calendar;
        this.isDefaultTag = isDefaultTag;
        this.isPersonalTag = isPersonalTag;
    }

    public void updateTag(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public CalendarTagInfo getInfoDTO() {
        return new CalendarTagInfo(
                this.id,
                this.name,
                this.color,
                this.isDefaultTag,
                this.isPersonalTag
        );
    }

}
