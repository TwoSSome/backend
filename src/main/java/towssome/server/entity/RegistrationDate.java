package towssome.server.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//엔티티 생성 날짜가 아닌, 사용자가 직접 등록한 날짜
public class RegistrationDate {

    int registrationYear;
    int registrationMonth;
    int registrationDay;

    public RegistrationDate(int registrationYear, int month, int registrationDay) {
        this.registrationYear = registrationYear;
        this.registrationMonth = month;
        this.registrationDay = registrationDay;
    }
}
