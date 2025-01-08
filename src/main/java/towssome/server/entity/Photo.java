package towssome.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import towssome.server.enumrated.PhotoType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    //사진의 원래 이름
    private String originalName;
    //중복되지 않는 유니크한 이름
    private String s3Name;
    private String s3Path;
    @Enumerated(value = EnumType.STRING)
    private PhotoType flag;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "review_id")
    private ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "community_id")
    private CommunityPost communityPost;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "calendar_post_id")
    private CalendarPost calendarPost;

    @OneToOne(mappedBy = "photo")
    @Setter
    private VoteAttribute voteAttribute;

    public Photo(String originalName, String s3Name, String s3Path, PhotoType flag, ReviewPost reviewPost, CommunityPost communityPost, CalendarPost calendarPost) {
        this.originalName = originalName;
        this.s3Name = s3Name;
        this.s3Path = s3Path;
        this.flag = flag;
        this.reviewPost = reviewPost;
        this.communityPost = communityPost;
        this.calendarPost = calendarPost;
    }
}
