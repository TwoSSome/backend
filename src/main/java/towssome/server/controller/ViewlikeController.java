package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReviewPostRes;
import towssome.server.entity.Member;
import towssome.server.service.MemberService;
import towssome.server.service.ReviewPostService;
import towssome.server.service.ViewlikeService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewlikeController {
    private static final int PAGE_SIZE = 10;
    private final ViewlikeService viewlikeService;
    private final ReviewPostService reviewPostService;
    private final MemberAdvice memberAdvice;

    /** 좋아요 기능 처리, 최근 조회는 ReviewPostController에 포함 */
    @PostMapping("/like/{reviewId}")
    public ResponseEntity<?> like(@PathVariable Long reviewId) {
        Member member = memberAdvice.findJwtMember();
        viewlikeService.likeProcess(reviewPostService.getReview(reviewId), member);
        return ResponseEntity.ok("like process success");
    }


}
