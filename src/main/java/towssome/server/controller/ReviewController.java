package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

    private final ReviewPostService reviewPostService;
    private final PhotoService photoService;
    private final MemberAdvice memberAdvice;
    private final ViewlikeService viewlikeService;
    private static final int PAGE_SIZE = 5;
    private final HashtagClassificationService hashtagClassificationService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createReview(@RequestPart(value = "body") ReviewPostReq req,
                                          @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException { // additional implementation needed for session
        log.info("reviewPostDTO = {}", req);

        Member jwtMember = memberAdvice.findJwtMember();
        reviewPostService.createReview(req, photos, jwtMember);
        return new ResponseEntity<>("Review Create Complete", HttpStatus.OK);
    }

    /** 특정리뷰글 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewPostRes> getReview(@PathVariable Long reviewId){ // get review by reviewId
        Member member = memberAdvice.findJwtMember();
        ReviewPost review = reviewPostService.getReview(reviewId);
        log.info("review = {}",review.getId());
        List<PhotoInPost> photo = photoService.getPhotoS3Path(review);
        ReviewPostRes reviewRes;

        //비회원 조회
        if (member == null) {
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    review.getMember().getId(),
                    photo,
                    false,
                    false,
                    false,
                    hashtagClassificationService.getHashtags(reviewId),
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy()
                    );
        }else {
            //회원 조회
            viewlikeService.viewProcess(review, member);
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    review.getMember().getId(),
                    photo,
                    reviewPostService.isMyPost(member, review),
                    viewlikeService.isLikedPost(member, review),
                    viewlikeService.isBookmarkedPost(member, review),
                    hashtagClassificationService.getHashtags(reviewId),
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy()
            );
        }

        return new ResponseEntity<>(reviewRes, HttpStatus.OK);
    }

    /** 리뷰글 수정 */
    @PostMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestPart(value = "body") ReviewPostUpdateReq req,
                                          @RequestPart(value = "photos") List<MultipartFile> addPhotos) throws IOException { // update review by reviewId
        Member member = memberAdvice.findJwtMember();
        if(!(reviewPostService.getReview(reviewId).getMember() == member)){
            return new ResponseEntity<>("You are not the author of this review", HttpStatus.FORBIDDEN);
        }
        reviewPostService.updateReview(reviewId, req, addPhotos);
        return new ResponseEntity<>("update complete", HttpStatus.OK);
    }

    /** 리뷰글 삭제 */
    @PostMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) { // delete review by reviewId
        Member member = memberAdvice.findJwtMember();
        if(!(reviewPostService.getReview(reviewId).getMember() == member)){
            return new ResponseEntity<>("You are not the author of this review", HttpStatus.FORBIDDEN);
        }
        ReviewPost review = reviewPostService.getReview(reviewId);
        reviewPostService.deleteReview(review);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** 리뷰글 전체 조회
     * @param cursorId (cursorId보다 작은 리뷰글들을 가져옴)
     * @param size (가져올 리뷰글의 개수)
     * @param recommend (추천) true or false
     */
    @GetMapping
    public CursorResult<ReviewSimpleRes> getReviews(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                  @RequestParam(value = "size", required = false) Integer size,
                                                  @RequestParam(value = "recommend", required = false) Boolean recommend) {
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getRecentReviewPage(cursorId, recommend, PageRequest.of(0, size));
    }

    /**
     * 구독 계정의 리뷰글 전체 조회
     * @param page
     * @return
     */
    @GetMapping("/subscribe")
    public CursorResult<ReviewSimpleRes> getSubscribeReviews(@RequestParam int page){

        Member jwtMember = memberAdvice.findJwtMember();

        return reviewPostService.getSubscribeReview(jwtMember,page);
    }


    /** 해시태그 검색 */
    @GetMapping("/search")
    public CursorResult<ReviewPostRes> keywordSearch(@RequestParam(value="keyword") String keyword,
                                                     @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                     @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        if (size == null) size = PAGE_SIZE;
        return hashtagClassificationService.getReviewPageByHashtag(keyword, cursorId, sort, PageRequest.of(0,size));
    }

}
