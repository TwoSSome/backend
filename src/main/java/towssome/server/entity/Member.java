package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;
    private String nickName;
    private int point;
    private int rankPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo profilePhoto;

    private String role;

    public Member(String username, String password, String nickName, int point, Photo profilePhoto, String role) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.point = point;
        this.profilePhoto = profilePhoto;
        this.role = role;
        this.rankPoint = 0;
    }

    public void changeProfilePhoto(Photo photo){
        this.profilePhoto = photo;
    }

    public void changeProfile(String nickName) {
        this.nickName = nickName;
    }

    public void addRankPoint(int point) {
        this.rankPoint += point;
    }

}
