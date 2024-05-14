package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.ReviewPost;

import java.util.List;

/**
 * 커뮤니티글 생성시 요청되는 dto
 * @param title
 * @param body
 * @param reviewPostId 인용된 review id 값, 없으면 null 필요
 */
public record CommunityPostSaveReq(
        String title,
        String body,
        boolean isAnonymous,
        Long reviewPostId,
        VoteSaveReq voteSaveReq
) {
}
