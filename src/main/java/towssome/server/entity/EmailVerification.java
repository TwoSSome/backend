package towssome.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import towssome.server.enumrated.EmailType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    Long id;
    String email;
    int authNum;
    EmailType emailType;

    public EmailVerification(String email, int authNum, EmailType emailType) {
        this.email = email;
        this.authNum = authNum;
        this.emailType = emailType;
    }
}
