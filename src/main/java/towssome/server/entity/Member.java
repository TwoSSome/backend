package towssome.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Setter
    private String socialId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo profilePhoto;

    // -----deprecated-----
    // 종설 4 백엔드 페이지 7
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "virtual_photo_id")
//    private Photo virtualPhoto;
//
//    private String virtualMateName;

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

    public void changeRole(String role) {
        this.role = role;
    }

    /**
     * 소셜 프로필 초기 설정
     */
    public void initialSocialProfile(String username, String nickname) {
        this.username = username;
        this.nickName = nickname;
    }

    // -----deprecated-----
    // 종설 4 백엔드 페이지 7
//    public void changeVirtualMateName(String name) {
//        this.virtualMateName = name;
//    }

}
