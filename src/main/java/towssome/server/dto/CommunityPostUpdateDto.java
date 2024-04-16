package towssome.server.dto;

import towssome.server.entity.ReviewPost;

/**
 * 커뮤니티 글 수정시 서비스로 넘겨주는 dto
 * @param id
 * @param title
 * @param body
 * @param quotation
 */
public record CommunityPostUpdateDto(
        Long id,
        String title,
        String body,
        ReviewPost quotation
) {

}
