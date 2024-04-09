package towssome.server.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String memberUsingId;
    String passwd;
    String nickName;
    int point;
    String profilePhotoPath;

    public Member(String memberUsingId, String passwd, String nickName, int point, String profilePhotoPath) {
        this.memberUsingId = memberUsingId;
        this.passwd = passwd;
        this.nickName = nickName;
        this.point = point;
        this.profilePhotoPath = profilePhotoPath;
    }
}
