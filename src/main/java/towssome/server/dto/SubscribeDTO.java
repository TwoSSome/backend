package towssome.server.dto;

import java.time.LocalDateTime;

public record SubscribeDTO(
        Long subscribeId,
        LocalDateTime subscribeDate
) {
}
