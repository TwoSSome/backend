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
    private String profilePhotoPath;

    private String role;

    public Member(String username, String password, String nickName, int point, String profilePhotoPath, String role) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.point = point;
        this.profilePhotoPath = profilePhotoPath;
        this.role = role;
    }
}
