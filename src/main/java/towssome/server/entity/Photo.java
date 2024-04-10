package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import towssome.server.enumrated.PhotoType;

@Entity
@Getter
@NoArgsConstructor
public class Photo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    Long id;

    //사진의 원래 이름
    String originalName;
    //중복되지 않는 유니크한 이름
    String s3Name;
    String s3Path;
    @Enumerated(value = EnumType.STRING)
    PhotoType flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    CommunityPost communityPost;

    public Photo(String originalName, String s3Name, String s3Path, PhotoType flag, ReviewPost reviewPost, CommunityPost communityPost) {
        this.originalName = originalName;
        this.s3Name = s3Name;
        this.s3Path = s3Path;
        this.flag = flag;
        this.reviewPost = reviewPost;
        this.communityPost = communityPost;
    }
}
