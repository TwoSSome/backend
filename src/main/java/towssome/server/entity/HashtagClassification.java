package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class HashtagClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    HashTag hashTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;
}
