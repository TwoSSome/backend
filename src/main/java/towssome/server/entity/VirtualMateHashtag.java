package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VirtualMateHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "virtual_mate_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    public VirtualMateHashtag(Member member, HashTag hashTag) {
        this.member = member;
        this.hashTag = hashTag;
    }
}
