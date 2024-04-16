package towssome.server.dto;

import towssome.server.entity.ReviewPost;

public record CommunityPostUpdateDto(
        Long id,
        String title,
        String body,
        ReviewPost quotation
) {

}
