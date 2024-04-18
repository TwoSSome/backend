package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteAttribute {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_attribute_id")
    Long id;

    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    Vote vote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    Photo photo;

    @OneToMany(mappedBy = "voteAttribute")
    List<VoteAttributeMember> voteAttributeMembers = new ArrayList<>();

    public VoteAttribute(String title, Vote vote, Photo photo) {
        this.title = title;
        this.vote = vote;
        this.photo = photo;
    }

    public void addMember(VoteAttributeMember member ) {
        voteAttributeMembers.add(member);
    }
}
