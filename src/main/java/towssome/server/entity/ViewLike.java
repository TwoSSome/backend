package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class ViewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viewlike_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;

    Boolean viewFlag;
    Boolean likeFlag;
}
