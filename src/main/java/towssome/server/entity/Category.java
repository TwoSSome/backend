package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_category_id")
    private Category master_category;

    @OneToMany(mappedBy = "master_category")
    private List<Category> slave_categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMasterCategory() {
        master_category.getSlave_categories().add(this);
    }

    public Category(String name, Member member) {
        this.name = name;
        this.member = member;
    }

    public Category(String name, Category master_category, Member member) {
        this.name = name;
        this.master_category = master_category;
        this.member = member;
    }

}
