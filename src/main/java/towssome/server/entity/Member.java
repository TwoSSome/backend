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

    private String memberUsingId;
    private String passwd;
    private String nickName;
    private int point;
    private String profilePhotoPath;

    public Member(String memberUsingId, String passwd, String nickName, int point, String profilePhotoPath) {
        this.memberUsingId = memberUsingId;
        this.passwd = passwd;
        this.nickName = nickName;
        this.point = point;
        this.profilePhotoPath = profilePhotoPath;
    }
}
