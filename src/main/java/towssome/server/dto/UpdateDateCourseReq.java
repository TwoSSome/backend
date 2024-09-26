package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "데이트코스 업데이트 요청")
public record UpdateDateCourseReq(
        @NotNull(message = "비어있을수 없습니다")
        Long id,
        @Size(max = 100, message = "최대 100자 이하여야합니다")
        String body
) {
}
