package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class Picture {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pitcture_id")
    Long id;

    //중복되지 않는 유니크한 파일네임
    String fileName;
    String s3Path;
    boolean flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    CommunityPost communityPost;

}
