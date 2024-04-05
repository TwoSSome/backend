package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CommunityPost extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "community_id")
    Long id;
    String title;
    String Body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    ReviewPost quotation;


}
