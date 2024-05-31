package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.enumrated.ReviewType;

@Entity
@Getter
@NoArgsConstructor
public class ReviewPost extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Lob
    private String body;
    private int price;

    @Enumerated(value = EnumType.STRING)
    private ReviewType reviewType;

    private String whereBuy;

    private int starPoint;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public ReviewPost(String body, int price, ReviewType reviewType, String whereBuy, int starPoint, String category, Member member) {
        this.body = body;
        this.price = price;
        this.reviewType = reviewType;
        this.whereBuy = whereBuy;
        this.starPoint = starPoint;
        this.category = category;
        this.member = member;
    }

    public ReviewPost(String body, int price, Member member){
        this.body = body;
        this.price = price;
        this.member = member;
    }
}
