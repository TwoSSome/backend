package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReviewSimpleRes;
import towssome.server.entity.Member;
import towssome.server.service.ReviewPostService;
import towssome.server.service.ViewlikeService;

@Tag(name = "좋아요/북마크")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewlikeController {
    private static final int PAGE_SIZE = 10;
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

    /**
     * 조회 기록 및 좋아요 조회
     */
    @Operation(summary = "조회 기록 및 좋아요 기록 조회")
    @GetMapping("/view/my")
    public CursorResult<ReviewSimpleRes> getRecentView(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                       @RequestParam(value = "size", required = false) Integer size,
                                                       @RequestParam(value = "sort", required = false) String sort) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getRecentView(member, cursorId, sort, PageRequest.of(0, size));
    }

    @GetMapping("/like/my")
    public CursorResult<ReviewSimpleRes> getLike(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                               @RequestParam(value = "size", required = false) Integer size,
                                               @RequestParam(value = "sort", required = false) String sort) {
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getLike(member, cursorId, sort, PageRequest.of(0, size));
    }
}
