package towssome.server.dto;

import java.util.List;

public record CursorResult<T>(
        List<T> values,
        Long nextPageId,
        boolean hasNext) {
}
