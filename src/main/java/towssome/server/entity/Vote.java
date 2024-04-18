package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    Long id;

    String title;

    @OneToOne(mappedBy = "vote")
    CommunityPost communityPost;

    public Vote(String title, CommunityPost communityPost) {
        this.title = title;
        this.communityPost = communityPost;
    }
}
