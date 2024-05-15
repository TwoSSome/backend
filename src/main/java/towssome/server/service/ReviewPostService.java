package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.repository.ReviewPostRepository;

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


    public void createReview(ReviewPostReq reviewReq, List<MultipartFile> photos, String username) throws IOException {
        Member member = memberService.getMember(username);
        ReviewPost reviewPost = new ReviewPost(
                reviewReq.body(),
                reviewReq.price(),
                member
        );
        reviewPostRepository.save(reviewPost);
        hashtagService.createHashtag(reviewPost, reviewPost.getBody());
        photoService.saveReviewPhoto(photos, reviewPost);
    }

    public ReviewPost getReview(Long reviewId) {
        return reviewPostRepository.findById(reviewId).orElseThrow(() ->
                new NotFoundReviewPostException("해당 리뷰글이 존재하지 않습니다."));
    }

    /**무한스크롤 */
    public CursorResult<ReviewPostRes> getReviewPage(Long cursorId, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final List<ReviewPost> reviewPosts = getReviewPosts(cursorId, page);
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    review.getMember().getId(),
                    photoService.getPhotoS3Path(review),
                    false,
                    false,
                    false,
                    hashtagService.getHashtags(review.getId())));
        }

        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPostRes, hasNext(lastIdOfList));
    }

    public boolean isMyPost(Member member, ReviewPost reviewPost) {
        return reviewPostRepository.findAllByMember(member).contains(reviewPost);
    }

    /**
     * Get review posts
     * @param id cursor id
     * @param page page
     *            if cursor_id is null -> review_id로 내림차순 정렬 후 테이블 가장 위에서부터 페이지 return
     *            else -> cursor_id 보다 작은 review_id를 가진 항목들을 내림차순 정렬 후 페이지 return
     * @return review posts
     */

    private List<ReviewPost> getReviewPosts(Long id, Pageable page) {
        return id == null ?
                this.reviewPostRepository.findAllByOrderByIdDesc(page) :
                this.reviewPostRepository.findByIdLessThanOrderByIdDesc(id, page);
    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return this.reviewPostRepository.existsByIdLessThan(id);
    }

    @Transactional
    public void deleteReview(ReviewPost review) {
        photoService.deletePhotos(review);
        hashtagService.deleteAllHashtags(review.getId());
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

    /**Get My Posts*/
    public CursorResult<ReviewPostRes> getMyReviewPage(Member member, Long cursorId, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final List<ReviewPost> reviewPosts = getMyReviewPosts(member.getId(), cursorId, page);
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    member.getId(),
                    photoService.getPhotoS3Path(review),
                    true,
                    false,
                    false,
                    hashtagService.getHashtags(review.getId())));
        }

        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPostRes, hasNext(lastIdOfList));
    }

    private List<ReviewPost> getMyReviewPosts(Long memberId, Long cursorId, Pageable page) {
        return cursorId == null ?
                reviewPostRepository.findMyPostAllByMemberId(memberId, page) :
                reviewPostRepository.findByMemberIdLessThanOrderByIdDesc(memberId, cursorId, page);
    }

    /**검색으로 얻은 포스트*/
    public CursorResult<ReviewPostRes> getSearchReviewPage(String keyword, Long cursorId, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final List<ReviewPost> reviewPosts = getSearchReviewPosts(keyword, cursorId, page);
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    review.getMember().getId(),
                    photoService.getPhotoS3Path(review),
                    false,
                    false,
                    false,
                    hashtagService.getHashtags(review.getId())));
        }

        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPostRes, hasNext(lastIdOfList));
    }

    private List<ReviewPost> getSearchReviewPosts(String keyword, Long cursorId, Pageable page) {
        return cursorId == null ?
                reviewPostRepository.findByKeywordContainingOrderByIdDesc(keyword, page) :
                reviewPostRepository.findByKeywordContainingAndIdLessThanOrderByIdDesc(keyword, cursorId, page);
    }
}
