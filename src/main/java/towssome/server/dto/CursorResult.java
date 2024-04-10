package towssome.server.dto;

import java.util.List;

public record CursorResult<T>(
        List<T> values,
        boolean hasNext) {
}
