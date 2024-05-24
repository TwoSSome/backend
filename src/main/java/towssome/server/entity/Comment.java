package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewPost reviewPost;

    private Boolean fixFlag;

    public Comment(String body, Member member, ReviewPost reviewPost){
        this.body = body;
        this.member = member;
        this.reviewPost = reviewPost;
        fixFlag = false;
    }

    @PrePersist
    public void prePersist() {
        if (this.fixFlag == null) {
            this.fixFlag = false;
        }
    }
}
