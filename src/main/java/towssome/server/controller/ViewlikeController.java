package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ViewLikeReq;
import towssome.server.entity.ReviewPost;
import towssome.server.service.ReviewPostService;
import towssome.server.service.ViewlikeService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewlikeController {
    private static final int PAGE_SIZE = 10;
    private final ViewlikeService viewlikeService;
    private final ReviewPostService reviewPostService;

    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody ViewLikeReq req) {
        viewlikeService.likeProcess(req);
        return ResponseEntity.ok("like success");
    }

    @GetMapping("/view/{memberId}") // EX) review?cursorId=10&size=10 -> id 10번 리뷰글 id보다 작은 10개의 리뷰글(id = 1~9)을 가져옴
    public CursorResult<ReviewPost> getRecentView(@PathVariable Long memberId, Long cursorId, Integer size) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getRecentView(memberId, cursorId, PageRequest.of(0, size));
    }

    @GetMapping("/like/{memberId}")
    public CursorResult<ReviewPost> getLike(@PathVariable Long memberId, Long cursorId, Integer size) {
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getLike(memberId, cursorId, PageRequest.of(0, size));
    }
}
