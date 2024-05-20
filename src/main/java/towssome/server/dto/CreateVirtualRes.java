package towssome.server.dto;

import java.util.List;

public record CreateVirtualRes(
        List<Long> hashtagList
) {
}
