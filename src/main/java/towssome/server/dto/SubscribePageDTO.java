package towssome.server.dto;

import towssome.server.entity.Subscribe;

import java.util.List;

public record SubscribePageDTO(
        List<Subscribe> subscribes,
        boolean hasNext
) {
}
