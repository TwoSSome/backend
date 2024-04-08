package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class Reply extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    Long id;

    String body;

    @ManyToOne
    @JoinColumn(name = "community_id")
    CommunityPost quotation;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member author;

}
