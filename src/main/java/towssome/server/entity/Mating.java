package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.enumrated.MatingStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mating extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mating_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member offerMember;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member obtainMember;

    @Enumerated(value = EnumType.STRING)
    private MatingStatus status;

    public Mating(Member offerMember, Member obtainMember, MatingStatus status) {
        this.offerMember = offerMember;
        this.obtainMember = obtainMember;
        this.status = status;
    }

    public void acceptOffer() {
        this.status = MatingStatus.MATING;
    }

}
