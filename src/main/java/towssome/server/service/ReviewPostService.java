package towssome.server.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.enumrated.ReviewType;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.exception.NotMatchReviewTypeException;
import towssome.server.repository.ReviewPostRepository;
import towssome.server.repository.ReviewPostRepositoryCustom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewPostService {

    private final ReviewPostRepository reviewPostRepository;
    private final MemberService memberService;
    private final PhotoService photoService;
    private final HashtagService hashtagService;
    private final HashtagClassificationService hashtagClassificationService;
    private final ReviewPostRepositoryCustom reviewPostRepositoryCustom;


    public void createReview(
            ReviewPostReq reviewReq,
            List<MultipartFile> photos,
            Member member) throws IOException {

        ReviewType reviewType = null;
        if (reviewReq.reviewType().equals("RECEIVED")) {
            reviewType = ReviewType.RECEIVED;
        } else if (reviewReq.reviewType().equals("GIVEN")) {
            reviewType = ReviewType.GIVEN;
        } else {
            throw new NotMatchReviewTypeException("정해진 타입이 아닙니다");
        }

        ReviewPost reviewPost = new ReviewPost(
                reviewReq.body(),
                reviewReq.price(),
                reviewType,
                reviewReq.whereBuy(),
                reviewReq.startPoint(),
                member
        );
        reviewPostRepository.save(reviewPost);
        hashtagService.createHashtag(reviewPost);
        photoService.saveReviewPhoto(photos, reviewPost);
    }

    public ReviewPost getReview(Long reviewId) {
        return reviewPostRepository.findById(reviewId).orElseThrow(() ->
                new NotFoundReviewPostException("해당 리뷰글이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteReview(ReviewPost review) {
        photoService.deletePhotos(review);
        hashtagService.deleteHashtagByReviewPost(review);
        reviewPostRepository.delete(review);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewPostUpdateReq req, List<MultipartFile> addPhotos) throws IOException {
        if(!req.willDeletePhoto().isEmpty()) {
            List<Long> deletedPhotoIds = req.willDeletePhoto();
            for (Long deletedPhotoId : deletedPhotoIds)
                photoService.deletePhoto(deletedPhotoId);
        }

        ReviewPost reviewPost = getReview(reviewId);
        photoService.saveReviewPhoto(addPhotos, reviewPost);
        ReviewPostUpdateDto dto = new ReviewPostUpdateDto(
                reviewId,
                req.body(),
                req.price()
        );
        reviewPostRepository.updateReview(dto);
    }


    public boolean isMyPost(Member member, ReviewPost reviewPost) {
        return reviewPostRepository.findAllByMember(member).contains(reviewPost);
    }

    /**최근 리뷰글 or 추천 리뷰글 */
    public CursorResult<ReviewSimpleRes> getRecentReviewPage(Long cursorId, Boolean recommend, Pageable page) {
        List<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getRecentReviewPosts(cursorId, recommend, page);
        return getReviewSimpleResCursorResult(reviewSimpleRes, reviewPosts);
    }

    private CursorResult<ReviewSimpleRes> getReviewSimpleResCursorResult(List<ReviewSimpleRes> reviewSimpleRes, Page<ReviewPost> reviewPosts) {
        Long cursorId;
        for(ReviewPost review : reviewPosts) {
            List<PhotoInPost> bodyPhotos = photoService.getPhotoS3Path(review);
            String bodyPhoto = bodyPhotos.isEmpty() ? null : bodyPhotos.get(0).photoPath();

            reviewSimpleRes.add(new ReviewSimpleRes(
                    review.getMember().getProfilePhoto(),
                    review.getMember().getNickName(),
                    bodyPhoto,
                    hashtagClassificationService.getHashtags(review.getId())
            ));
        }
        cursorId = reviewPosts.isEmpty() ?
                null : reviewPosts.getContent().get(reviewPosts.getContent().size() - 1).getId();
        return new CursorResult<>(reviewSimpleRes, cursorId, reviewPosts.hasNext());
    }

    /**
     * Get review posts
     *
     * @param cursorId cursor id
     * @param page     page
     *                 if cursor_id is null -> review_id로 내림차순 정렬 후 테이블 가장 위에서부터 페이지 return
     *                 else -> cursor_id 보다 작은 review_id를 가진 항목들을 내림차순 정렬 후 페이지 return
     * @return review posts
     */

    private Page<ReviewPost> getRecentReviewPosts(Long cursorId, Boolean recommend, Pageable page) {
        return cursorId == null ?
                reviewPostRepositoryCustom.findFirstPageByOrderByReviewIdDesc(recommend, page) : // cursor_id가 null이면 가장 최신의 리뷰글부터 페이지를 가져옴 -> 최초 요청
                reviewPostRepositoryCustom.findByCursorIdLessThanOrderByReviewIdDesc(cursorId, recommend, page); // cursor_id가 null이 아니면 cursor_id보다 작은 리뷰글부터 페이지를 가져옴
    }

    /**Get My Posts*/
    public CursorResult<ReviewSimpleRes> getMyReviewPage(Member member, Long cursorId, String sort, Pageable page) {
        List<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getMyReviewPosts(member.getId(), cursorId, sort, page);
        return getReviewSimpleResCursorResult(reviewSimpleRes, reviewPosts);
    }

    private Page<ReviewPost> getMyReviewPosts(Long memberId, Long cursorId, String sort, Pageable page) {
        return cursorId == null ?
                reviewPostRepositoryCustom.findMyPostFirstPageByMemberId(memberId, sort, page) :
                reviewPostRepositoryCustom.findByMemberIdLessThanOrderByIdDesc(memberId, cursorId, sort, page);
    }
}
