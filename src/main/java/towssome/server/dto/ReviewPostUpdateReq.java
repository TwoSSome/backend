package towssome.server.dto;

import java.util.List;

public record ReviewPostUpdateReq(
        String body,
        int price,
        Long memberId,
        List<Long> willDeletePhoto
) {
}
