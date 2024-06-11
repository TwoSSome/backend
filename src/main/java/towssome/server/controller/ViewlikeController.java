package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.advice.MemberAdvice;
import towssome.server.entity.Member;
import towssome.server.service.ReviewPostService;
import towssome.server.service.ViewlikeService;

@Tag(name = "좋아요/북마크")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewlikeController {
    private final ViewlikeService viewlikeService;
    private final ReviewPostService reviewPostService;
    private final MemberAdvice memberAdvice;

    /** 좋아요 기능 처리, 최근 조회는 ReviewPostController에 포함 */
    @Operation(summary = "좋아요 처리 API", description = "좋아요 되어 있지않으면 좋아요 처리, 이미 처리 되어 있으면 취소")
    @PostMapping("/like/{reviewId}")
    public ResponseEntity<?> like(@PathVariable Long reviewId) {
        Member member = memberAdvice.findJwtMember();
        viewlikeService.likeProcess(reviewPostService.getReview(reviewId), member);
        return ResponseEntity.ok("like process success");
    }
}
