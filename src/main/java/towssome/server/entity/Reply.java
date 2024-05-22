package towssome.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    private String body;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private ReviewPost quotation;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityPost communityPost;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    boolean isAnonymous;
    @Setter
    boolean isAdoption;

    public Reply(String body, ReviewPost quotation, CommunityPost communityPost, Member author, boolean isAnonymous) {
        this.body = body;
        this.quotation = quotation;
        this.communityPost = communityPost;
        this.author = author;
        this.isAnonymous = isAnonymous;
        this.isAdoption = false;
    }

    public void update(String body, ReviewPost quotation) {
        this.body = body;
        this.quotation = quotation;
    }

}
