package towssome.server.dto;

import towssome.server.enumrated.ReviewType;

public record ReviewPostReq(
        String body,
        int price,
        String whereBuy,
        int startPoint,
        String reviewType
) {
}
