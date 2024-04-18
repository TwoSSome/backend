package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteAttributeMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_attribute_user_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_attribute_id")
    VoteAttribute voteAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    public VoteAttributeMember(VoteAttribute voteAttribute, Member member) {
        this.voteAttribute = voteAttribute;
        this.member = member;
        this.voteAttribute.getVoteAttributeMembers().add(this);
    }
}
