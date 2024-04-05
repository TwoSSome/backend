package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class ReviewPost extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    Long id;

    String body;
    int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    User user;

}
