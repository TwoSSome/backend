package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private CommunityPost communityPost;

    @OneToMany(mappedBy = "vote", orphanRemoval = true)
    @Setter
    private List<VoteAttribute> voteAttributes = new ArrayList<>();

    public Vote(String title, CommunityPost communityPost) {
        this.title = title;
        this.communityPost = communityPost;
    }
}
