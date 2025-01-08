package towssome.server.dto;

import java.util.List;

public record UpdateCalendarPostDTO(
        Long id,
        String title,
        String body,
        List<Long> deletePhoto
) {
}
