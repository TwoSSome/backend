package towssome.server.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 조회 시 응답하는 dto
 * @param title
 * @param body
 * @param createTime 생성 시간
 * @param lastModifiedTime 업데이트 시간
 * @param photoPaths 사진 객체의 id와 S3 경로
 * @param reviewPostId 인용된 review의 id
 *                     -> 프론트에서는 id만 가지고 post 요청으로 따로 정보를 받아올 수 있을까?
 */
public record CommunityPostRes(
    String title,
    String body,
    LocalDateTime createTime,
    LocalDateTime lastModifiedTime,
    List<PhotoInPost> photoPaths,
    Long reviewPostId,
    VoteReq vote;
) {
}
