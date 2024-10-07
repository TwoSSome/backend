package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cluster extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cluster_id")
    private Long id;

    @Column(name = "cluster_num")
    private Long clusterNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Cluster(Long clusterNum, Member member) {
        this.clusterNum = clusterNum;
        this.member = member;
    }
}
