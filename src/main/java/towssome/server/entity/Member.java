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
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo profilePhoto;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "virtual_photo_id")
    private Photo virtualPhoto;

    private String virtualMateName;

    private String role;

    public Member(String username, String password, String nickName, int point, Photo profilePhoto, String role, String email) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.point = point;
        this.profilePhoto = profilePhoto;
        this.role = role;
        this.rankPoint = 0;
        this.email = email;
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

    public void changeVirtualPhoto(Photo photo) {
        this.virtualPhoto = photo;
    }

    public void changeVirtualMateName(String name) {
        this.virtualMateName = name;
    }

}
