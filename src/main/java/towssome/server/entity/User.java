package towssome.server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class User extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    String userId;
    String passwd;
    String nickName;
    int point;
    String profilePhotoPath;


}
