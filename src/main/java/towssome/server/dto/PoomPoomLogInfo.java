package towssome.server.dto;

import java.time.LocalDate;

public record PoomPoomLogInfo(
        Long postId,
        PhotoInPost photoInPost,
        LocalDate startDate,
        LocalDate endDate
) {
}
