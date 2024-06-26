package towssome.server.dto;

import java.util.List;

public record PageResult<T>(
        List<T> values,
        long totalCount,
        int totalPages,
        int currentPage,
        int pageSize
) {
}
