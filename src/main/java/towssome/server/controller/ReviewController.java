package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.*;
import towssome.server.entity.HashtagClassification;
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
    private final MemberService memberService;
    private final ViewlikeService viewlikeService;
    private static final int PAGE_SIZE = 10;
    private final HashtagClassificationService hashtagClassificationService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createReview(@RequestPart(value = "body") ReviewPostReq req,
                                          @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException { // additional implementation needed for session
        log.info("reviewPostDTO = {}", req);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewPostService.createReview(req, photos, username);
        return new ResponseEntity<>("Review Create Complete", HttpStatus.OK);
    }

    /** 특정리뷰글 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewPostRes> getReview(@PathVariable Long reviewId){ // get review by reviewId
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewPost review = reviewPostService.getReview(reviewId);
        log.info("review = {}",review.getId());
        List<PhotoInPost> photo = photoService.getPhotoS3Path(review);
        ReviewPostRes reviewRes;

        //비회원 조회
        if (name.equals("anonymousUser")) {
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    review.getMember().getId(),
                    photo,
                    false,
                    false,
                    false,
                    hashtagClassificationService.getHashtags(reviewId)
                    );
        }else {
            //회원 조회
            Member member = memberService.getMember(name);
            viewlikeService.viewProcess(review, member);
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    review.getMember().getId(),
                    photo,
                    reviewPostService.isMyPost(member, review),
                    viewlikeService.isLikedPost(member, review),
                    viewlikeService.isBookmarkedPost(member, review),
                    hashtagClassificationService.getHashtags(reviewId)
            );
        }

        return new ResponseEntity<>(reviewRes, HttpStatus.OK);
    }

    @PostMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestPart(value = "body") ReviewPostUpdateReq req,
                                          @RequestPart(value = "photos") List<MultipartFile> addPhotos) throws IOException { // update review by reviewId
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!reviewPostService.getReview(reviewId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this review", HttpStatus.FORBIDDEN);
        }
        reviewPostService.updateReview(reviewId, req, addPhotos);
        return new ResponseEntity<>("update complete", HttpStatus.OK);
    }

    @PostMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) { // delete review by reviewId
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!reviewPostService.getReview(reviewId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this review", HttpStatus.FORBIDDEN);
        }
        ReviewPost review = reviewPostService.getReview(reviewId);
        reviewPostService.deleteReview(review);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping // EX) review?cursorId=10&size=10 -> id 10번 리뷰글 id보다 작은 10개의 리뷰글(id = 1~9)을 가져옴
    public CursorResult<ReviewPostRes> getReviews(Long cursorId, Integer size) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getReviewPage(cursorId, PageRequest.of(0, size));
    }

    @GetMapping("/my")
    public CursorResult<ReviewPostRes> getMyReviews(Long cursorId, Integer size) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMember(username);
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getMyReviewPage(member, cursorId, PageRequest.of(0, size));
    }

    @GetMapping("/search") // 해시태그 검색
    public PageResult<ReviewPostRes> keywordSearch(@RequestPart(value="keyword") String keyword,@RequestParam String sort, @RequestParam int page){
        Page<ReviewPost> result = hashtagClassificationService.getReviewPostByHashtag(keyword, sort, page-1, PAGE_SIZE);
        ArrayList<ReviewPostRes> reviewPostListRes = new ArrayList<>();
        for(ReviewPost reviewPost: result.getContent()){
            reviewPostListRes.add(new ReviewPostRes(
                    reviewPost.getBody(),
                    reviewPost.getPrice(),
                    reviewPost.getCreateDate(),
                    reviewPost.getLatsModifiedDate(),
                    reviewPost.getMember().getId(),
                    photoService.getPhotoS3Path(reviewPost),
                    false,
                    false,
                    false,
                    hashtagClassificationService.getHashtags(reviewPost.getId())
            ));
        }
        return new PageResult<>(
                reviewPostListRes,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()+1,
                PAGE_SIZE
        );
    }

}
