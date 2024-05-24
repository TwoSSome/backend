package towssome.server.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import towssome.server.dto.VoteRes;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long id;
    private String title;
    private String body;
    private boolean isAnonymous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewPost quotation;

    @OneToOne(mappedBy = "communityPost", orphanRemoval = true)
    private Vote vote;

    public CommunityPost(String title, String body, boolean isAnonymous, Member author, ReviewPost quotation) {
        this.title = title;
        this.body = body;
        this.isAnonymous = isAnonymous;
        this.author = author;
        this.quotation = quotation;
    }

    public void update(String title, String body, ReviewPost quotation) {
        this.title = title;
        this.body = body;
        this.quotation = quotation;
    }

    //NPE 문제 해결을 위한 Optional 사용
    public Optional<ReviewPost> getQuotation() {
        return Optional.ofNullable(quotation);
    }

    public Optional<Vote> getVote() {
        return Optional.ofNullable(vote);
    }

}
