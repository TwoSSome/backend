package towssome.server.dto;

import towssome.server.entity.Subscribe;

import java.util.List;

public record SubscribeSliceDTO(
        List<Subscribe> subscribes,
        boolean hasNext
) {
}
