package towssome.server.dto;


import towssome.server.enumrated.ReviewType;

public record ReviewPostUpdateDto(
        Long id,
        String body,
        int price,
        String whereBuy,
        String category,
        ReviewType reviewType
) {
}
