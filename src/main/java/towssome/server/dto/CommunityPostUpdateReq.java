package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @param title 수정되는 제목
 * @param body 수정되는 본문
 * @param deletedPhotoId 삭제된 사진 id, 없으면 null 필요
 * @param reviewPostId 현재 인용되어 있는 reviewPost id
 */
public record CommunityPostUpdateReq(
        String title,
        String body,
        List<Long> deletedPhotoId,
        Long reviewPostId
) {
}
