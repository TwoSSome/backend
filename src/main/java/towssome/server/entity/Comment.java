package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;
    String body;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;

    public Comment(String body, Member member, ReviewPost reviewPost){
        this.body = body;
        this.member = member;
        this.reviewPost = reviewPost;
    }
}
