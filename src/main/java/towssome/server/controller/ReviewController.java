package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.*;
import towssome.server.entity.ReviewPost;
import towssome.server.service.PhotoService;
import towssome.server.service.ReviewPostService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {
    private final ReviewPostService reviewPostService;
    private final PhotoService photoService;
    private static final int PAGE_SIZE = 10;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewPostReq req) throws IOException { // additional implementation needed for session
        log.info("reviewPostDTO = {}", req);
        ReviewPost reviewPost = reviewPostService.createReview(req);
        photoService.saveReviewPhoto(req.photos(), reviewPost);
        return new ResponseEntity<>("Review Create Complete", HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewPostRes> getReview(@PathVariable Long reviewId){ // get review by reviewId
        ReviewPost review = reviewPostService.getReview(reviewId);
        log.info("review = {}",review.getId());
        List<PhotoInPost> photo = photoService.getPhotoS3Path(review);
        ReviewPostRes reviewRes = new ReviewPostRes(
                review.getBody(),
                review.getPrice(),
                review.getCreateDate(),
                review.getLatsModifiedDate(),
                review.getMember().getId(),
                photo
        );
        return new ResponseEntity<>(reviewRes, HttpStatus.OK);
    }


    @PostMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewPostUpdateReq req) throws IOException { // update review by reviewId
        List<Long> deletedPhotoIds = req.willDeletePhoto();
        for (Long deletedPhotoId : deletedPhotoIds) {
            photoService.deletePhoto(deletedPhotoId);
        }
        List<MultipartFile> photo = req.willAddPhoto();
        ReviewPost post = reviewPostService.getReview(reviewId);
        photoService.saveReviewPhoto(photo, post);
        ReviewPostUpdateDto dto = new ReviewPostUpdateDto(
                reviewId,
                req.body(),
                req.price()
        );
        reviewPostService.updateReview(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) { // delete review by reviewId
        ReviewPost review = reviewPostService.getReview(reviewId);
        reviewPostService.deleteReview(review);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping // EX) review?cursorId=10&size=10 -> id 10번 리뷰글 id보다 작은 10개의 리뷰글(id = 1~9)을 가져옴
    public CursorResult<ReviewPost> getReviews(Long cursorId, Integer size) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        if(size == null) size = PAGE_SIZE;
        return this.reviewPostService.getReviewPage(cursorId, PageRequest.of(0, size));
    }
}
