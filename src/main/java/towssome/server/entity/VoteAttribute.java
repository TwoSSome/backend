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
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @OneToMany(mappedBy = "voteAttribute")
    private List<VoteAttributeMember> voteAttributeMembers = new ArrayList<>();

    public VoteAttribute(String title, Vote vote, Photo photo) {
        this.title = title;
        this.vote = vote;
        this.photo = photo;
        this.vote.getVoteAttributes().add(this);
        this.photo.setVoteAttribute(this);
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        photo.setVoteAttribute(this);
    }

}
