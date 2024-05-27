package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class ViewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viewlike_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewPost reviewPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "member_id")
    private Member member;

    private Boolean viewFlag;
    private int viewAmount;
    private Boolean likeFlag;

    public ViewLike(ReviewPost reviewPost, Member member, boolean view, boolean like) {
        this.reviewPost = reviewPost;
        this.member = member;
        this.viewFlag = view;
        this.likeFlag = like;
        this.viewAmount = 1;
    }

    public void addViewAmount() {
        this.viewAmount++;
    }

    public void setLike() {
        this.likeFlag = true;
    }

    public void setUnlike() {
        this.likeFlag = false;
    }
}
