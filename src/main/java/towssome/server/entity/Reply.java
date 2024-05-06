package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class Reply extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    private String body;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityPost quotation;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

}
