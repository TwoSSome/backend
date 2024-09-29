package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "데이트코스 생성 dto")
public record CreateDateCourseReq(
        @Schema(description = "100자 제한")
        String body,
        int year,
        int month,
        int day
) {
}
