package towssome.server.dto;

import lombok.Data;

public record ErrorResult(
        String exceptionName,
        String message
) {

}
