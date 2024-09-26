package towssome.server.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GPS {

    //위도
    Double longitude;
    //경도
    Double latitude;

    public GPS(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
