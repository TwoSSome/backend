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
    Long id;
    String title;
    String Body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost quotation;

    public CommunityPost(String title, String body, Member author, ReviewPost quotation) {
        this.title = title;
        Body = body;
        this.author = author;
        this.quotation = quotation;
    }
}
