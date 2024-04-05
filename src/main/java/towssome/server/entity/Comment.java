package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;
    String body;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost reviewPost;
}
