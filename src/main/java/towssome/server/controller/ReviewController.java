package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import towssome.server.exception.PageException;
import towssome.server.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "리뷰글", description = "리뷰글 API")
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

    @Operation(summary = "리뷰글 생성 API", description = "body : json, photos : multipart-form-data, body에서 reviewType은 반드시 GIVEN 또는 RECEIVED 일 것")
    @PostMapping(path = "/create")
    public ResponseEntity<CreateRes> createReview(@RequestPart(value = "body") ReviewPostReq req,
                                          @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException { // additional implementation needed for session
        log.info("reviewPostDTO = {}", req);

        Member jwtMember = memberAdvice.findJwtMember();
        ReviewPost review = reviewPostService.createReview(req, photos, jwtMember);
        return new ResponseEntity<>(new CreateRes(
                review.getId()
        ), HttpStatus.OK);
    }

    @Operation(summary = "리뷰글 단건 조회 API", description = "비회원 사용 가능, reviewId로 get")
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
    @Operation(summary = "리뷰글 수정 API",description = "리뷰글 수정",
    parameters = {@Parameter(name = "reviewId", description = "수정할 리뷰글 id"),
    @Parameter(name = "body", description = "ReviewPostUpdateReq 참고"),
    @Parameter(name = "photos", description = "새로 추가할 사진")})
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

    @Operation(summary = "리뷰글 삭제 API", description = "리뷰글 삭제 요청",
    parameters = @Parameter(name = "reviewId", description = "삭제할 리뷰 id"))
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
    @Operation(summary = "리뷰글 전체 조회 API",
            description = "리뷰글 전체 조회 API",
            parameters = {@Parameter(name = "cursorId", description = "cursorId보다 id가 작은 게시글 가져옴"),
            @Parameter(name = "size", description = "가져올 게시글의 갯수"),
            @Parameter(name = "recommend", description = "")})
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
    @Operation(summary = "구독 계정 리뷰글 조회 API",
            description = "구독한 계정들의 리뷰글 전체 조회 API",
            parameters = {@Parameter(name = "page", description = "최소 1이상, 한 페이지마다 10개의 리뷰글 가져옴") })
    @GetMapping("/subscribe")
    public CursorResult<ReviewSimpleRes> getSubscribeReviews(@RequestParam int page){

        if (page <= 0) {
            throw new PageException("페이지 번호는 0보다 커야 합니다");
        }
        Member jwtMember = memberAdvice.findJwtMember();

        return reviewPostService.getSubscribeReview(jwtMember,page);
    }

    @Operation(summary = "해시태그 검색 API", description = "cursorId를 기준으로 sort 값으로 size 만큼의 게시글을 검색",
    parameters = {@Parameter(name = "keyword", description = "검색할 해시태그 키워드"),
    @Parameter(name = "cursorId", description = "cursorId"),
    @Parameter(name = "sort", description = "최신순 정렬 desc(기본값), 오래된 순 정렬 asc"),
    @Parameter(name = "size", description = "가져올 게시글 수")})
    /** 해시태그 검색 */
    @GetMapping("/search")
    public CursorResult<ReviewSimpleRes> keywordSearch(@RequestParam(value="keyword") String keyword,
                                                     @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                     @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        if (size == null) size = PAGE_SIZE;
        return hashtagClassificationService.getReviewPageByHashtag(keyword, cursorId, sort, PageRequest.of(0,size));
    }

}
