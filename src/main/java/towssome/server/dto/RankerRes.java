package towssome.server.dto;

import java.util.List;

public record RankerRes(
        Long id,
        String nickname,
        String profilePhotoPath,
        List<String> hashtags,
        int rankPoint,
        int rank
) {
}
