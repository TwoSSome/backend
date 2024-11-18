package towssome.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 발생 시 반환되는 스키마")
public record ErrorResult(
        String exceptionName,
        String message
) {

}
