package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "community_id")
    private Long id;
    private String title;
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewPost quotation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    public CommunityPost(String title, String body, Member author, ReviewPost quotation) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.quotation = quotation;
    }

    public void update(String title, String body, ReviewPost quotation) {
        this.title = title;
        this.body = body;
        this.quotation = quotation;
    }

}
