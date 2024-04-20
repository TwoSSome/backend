package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReviewPost extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    Long id;

    String body;
    int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    public ReviewPost(String body, int price, Member member) {
        this.body = body;
        this.price = price;
        this.member = member;
    }

    public void update(String body, int price) {
        this.body = body;
        this.price = price;
    }
}
