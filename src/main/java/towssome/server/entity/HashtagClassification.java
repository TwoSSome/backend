package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashtagClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hashtag_classification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewPost reviewPost;

    public HashtagClassification(HashTag hashTag, ReviewPost reviewPost) {
        this.hashTag = hashTag;
        this.reviewPost = reviewPost;
    }
}
