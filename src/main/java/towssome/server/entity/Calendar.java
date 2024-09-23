package towssome.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar {

    @Id
    private String id;

    @OneToOne
    private Member authMember1;

    @OneToOne
    private Member authMember2;

    public Calendar(String id, Member authMember1, Member authMember2) {
        this.id = id;
        this.authMember1 = authMember1;
        this.authMember2 = authMember2;
    }
}
