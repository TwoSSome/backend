package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.ReviewPostDTO;
import towssome.server.entity.ReviewPost;
import towssome.server.service.ReviewPostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewPostService reviewPostService;

    @PostMapping("/review/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewPostDTO reviewPostDTO){ // 리뷰 작성(세션에 따른 추가 구현 필요)
        log.info("reviewPostDTO = {}", reviewPostDTO);
        reviewPostService.createReview(reviewPostDTO);
        return new ResponseEntity<>("Review Complete", HttpStatus.OK);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ReviewPostDTO> getReview(@PathVariable Long reviewId){ // 리뷰 조회
        ReviewPostDTO reviewPostDTO = reviewPostService.getReview(reviewId);
        return new ResponseEntity<>(reviewPostService.getReview(reviewId), HttpStatus.OK);
    }

    @GetMapping("/review")
    public ResponseEntity<List<ReviewPost>> getAllReviews() { // 모든 리뷰 조회
        List<ReviewPost> reviewPosts = reviewPostService.getReviewList();
        return ResponseEntity.ok(reviewPosts);
    }
}
