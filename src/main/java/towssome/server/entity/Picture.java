package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class Picture {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pitcture_id")
    Long id;

    String s3Path;
    boolean flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commuity_id")
    CommunityPost communityPost;

}
