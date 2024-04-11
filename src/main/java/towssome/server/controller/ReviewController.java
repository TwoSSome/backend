package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReviewPostDTO;
import towssome.server.entity.ReviewPost;
import towssome.server.service.ReviewPostService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {
    private final ReviewPostService reviewPostService;
    private static final int PAGE_SIZE = 10;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewPostDTO reviewPostDTO){ // additional implementation needed for session
        log.info("reviewPostDTO = {}", reviewPostDTO);
        reviewPostService.createReview(reviewPostDTO);
        return new ResponseEntity<>("Review Complete", HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewPostDTO> getReview(@PathVariable Long reviewId){ // get review by reviewId
        ReviewPostDTO reviewPostDTO = reviewPostService.getReview(reviewId);
        return new ResponseEntity<>(reviewPostDTO, HttpStatus.OK);
    }

    @GetMapping // EX) review?cursorId=10&size=10 -> id 10�� ����� id���� ���� 10���� �����(id = 1~9)�� ������
    public CursorResult<ReviewPost> getReviews(Long cursorId, Integer size) { // get all review(size ��ŭ�� ����۰� ���� ������� ���翩��(boolean) ����)
        if(size == null) size = PAGE_SIZE;
        return this.reviewPostService.getReviewPage(cursorId, PageRequest.of(0, size));
    }
}
