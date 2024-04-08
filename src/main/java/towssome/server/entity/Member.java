package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String memberUsingId;
    String passwd;
    String nickName;
    int point;
    String profilePhotoPath;


}
