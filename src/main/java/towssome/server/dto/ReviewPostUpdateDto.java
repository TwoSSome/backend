package towssome.server.dto;

public record ReviewPostUpdateDto(
        Long id,
        String body,
        int price
) {
}
