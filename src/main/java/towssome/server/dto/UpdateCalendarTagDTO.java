package towssome.server.dto;

public record UpdateCalendarTagDTO(
        Long id,
        String name,
        int color
) {
}