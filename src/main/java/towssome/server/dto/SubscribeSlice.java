package towssome.server.dto;

import java.util.List;

public record SubscribeSlice(
        List<SubscribeRes> list,
        boolean hasNext
) {
}
