package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.enumrated.ReviewType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ReviewPost extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(length = 1000)
    private String body;
    private int price;

    @Enumerated(value = EnumType.STRING)
    private ReviewType reviewType;

    private String whereBuy;

    private int starPoint;

    private String category;

    private String item;

    private String item_url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookMark> bookMarks = new ArrayList<>();

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ViewLike> viewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


    public ReviewPost(String body, int price, ReviewType reviewType, String whereBuy, int starPoint, String category, Member member, String item) {
        this.body = body;
        this.price = price;
        this.reviewType = reviewType;
        this.whereBuy = whereBuy;
        this.starPoint = starPoint;
        this.category = category;
        this.member = member;
        this.item = item;
    }

    public ReviewPost(String body, int price, ReviewType reviewType, String whereBuy, int starPoint, String category, Member member, String item, String item_url) {
        this.body = body;
        this.price = price;
        this.reviewType = reviewType;
        this.whereBuy = whereBuy;
        this.starPoint = starPoint;
        this.category = category;
        this.member = member;
        this.item = item;
        this.item_url = item_url;
    }

    public ReviewPost(String body, int price, Member member){
        this.body = body;
        this.price = price;
        this.member = member;
    }

    public void addBookMarks(BookMark bookMark) {
        bookMarks.add(bookMark);
    }

    public void removeBookMarks(BookMark bookMark) {
        bookMarks.remove(bookMark);
    }

    public void addViewLikes(ViewLike viewLike) {
        viewLikes.add(viewLike);
    }

    public void removeViewLikes(ViewLike viewLike) {
        viewLikes.remove(viewLike);
    }

    public void addComments(Comment comment) {
        comments.add(comment);
    }

    public void removeComments(Comment comment) {
        comments.remove(comment);
    }
}
