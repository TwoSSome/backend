package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ViewLikeReq;
import towssome.server.entity.ReviewPost;
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
    private final MemberService memberService;

    /** 좋아요 기능 처리 최근 조회는 ReviewPostController에 포함 */
    @PostMapping("/like/{reviewId}")
    public ResponseEntity<?> like(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        viewlikeService.likeProcess(reviewPostService.getReview(reviewId), memberService.getMember(username));
        return ResponseEntity.ok("like process success");
    }

    /** 조회 기록 및 좋아요 조회 */
    @GetMapping("/view/my")
    public CursorResult<ReviewPost> getRecentView(Long cursorId, Integer size) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long memberId = memberService.getMember(username).getId();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getRecentView(memberId, cursorId, PageRequest.of(0, size));
    }

    @GetMapping("/like/my")
    public CursorResult<ReviewPost> getLike(Long cursorId, Integer size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long memberId = memberService.getMember(username).getId();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getLike(memberId, cursorId, PageRequest.of(0, size));
    }
}
