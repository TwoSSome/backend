package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "데이트코스 업데이트 요청")
public record UpdateDateCourseReq(
        Long id,
        String body
) {
}
