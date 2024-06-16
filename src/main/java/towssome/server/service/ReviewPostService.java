package towssome.server.service;

import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import towssome.server.repository.SubscribeRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewPostService {

    private final ReviewPostRepository reviewPostRepository;
    private final ViewlikeService viewlikeService;
    private final PhotoService photoService;
    private final HashtagService hashtagService;
    private final HashtagClassificationService hashtagClassificationService;
    private final SubscribeRepository subscribeRepository;

    public ReviewPost createReview(
            ReviewPostReq reviewReq,
            List<MultipartFile> photos,
            Member member) throws IOException {

        ReviewType reviewType = switch (reviewReq.reviewType()) {
            case "RECEIVED" -> ReviewType.RECEIVED;
            case "GIVEN" -> ReviewType.GIVEN;
            default -> throw new NotMatchReviewTypeException("정해진 타입이 아닙니다");
        };

        ReviewPost reviewPost = new ReviewPost(
                reviewReq.body(),
                reviewReq.price(),
                reviewType,
                reviewReq.whereBuy(),
                0,
                reviewReq.category(),
                member
        );
        reviewPostRepository.save(reviewPost);
        hashtagService.saveCategoryInHashtag(reviewPost, reviewReq.category());
        hashtagService.createHashtag(reviewPost);
        photoService.saveReviewPhoto(photos, reviewPost);
        return reviewPost;
    }

    public ReviewPost getReview(Long reviewId) {
        return reviewPostRepository.findById(reviewId).orElseThrow(() ->
                new NotFoundReviewPostException("해당 리뷰글이 존재하지 않습니다."));
    }

    public ReviewPostRes getReview(Long reviewId, Member member){
        ReviewPost review = getReview(reviewId);
        List<PhotoInPost> photo = photoService.getPhotoS3Path(review);
        ReviewPostRes reviewRes;

        List<HashtagRes> hashtags = new ArrayList<>();
        for(Tuple tuple : hashtagClassificationService.getHashtags(review.getId())) {
            hashtags.add(new HashtagRes(
                    tuple.get(0, Long.class),
                    tuple.get(1, String.class)
            ));
        }
        //비회원 조회
        if (member == null) {
            Member postMember = review.getMember();
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    postMember.getId(),
                    postMember.getProfilePhoto() != null ?
                            postMember.getProfilePhoto().getS3Path() :
                            null,
                    photo,
                    false,
                    false,
                    false,
                    hashtags,
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy(),
                    postMember.getNickName()
            );
        }else {
            //회원 조회
            viewlikeService.viewProcess(review, member);
            reviewRes = new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    member.getId(),
                    member.getProfilePhoto() != null ?
                            member.getProfilePhoto().getS3Path() :
                            null,
                    photo,
                    isMyPost(member, review),
                    viewlikeService.isLikedPost(member, review),
                    viewlikeService.isBookmarkedPost(member, review),
                    hashtags,
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy(),
                    member.getNickName()
            );
        }
        return reviewRes;
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
            String profilePhoto = review.getMember().getProfilePhoto() != null ?
                    review.getMember().getProfilePhoto().getS3Path() :
                    null;

            List<HashtagRes> hashtags = new ArrayList<>();
            for(Tuple tuple : hashtagClassificationService.getHashtags(review.getId())) {
                hashtags.add(new HashtagRes(
                        tuple.get(0, Long.class),
                        tuple.get(1, String.class)
                ));
            }

            reviewSimpleRes.add(new ReviewSimpleRes(
                    review.getId(),
                    review.getBody(),
                    profilePhoto,
                    review.getMember().getNickName(),
                    bodyPhoto,
                    hashtags
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
                reviewPostRepository.findFirstPageByOrderByReviewIdDesc(recommend, page) : // cursor_id가 null이면 가장 최신의 리뷰글부터 페이지를 가져옴 -> 최초 요청
                reviewPostRepository.findByCursorIdLessThanOrderByReviewIdDesc(cursorId, recommend, page); // cursor_id가 null이 아니면 cursor_id보다 작은 리뷰글부터 페이지를 가져옴
    }

    /**Get My Posts*/
    public CursorResult<ReviewSimpleRes> getMyReviewPage(Member member, Long cursorId, String sort, Pageable page) {
        List<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getMyReviewPosts(member.getId(), cursorId, sort, page);
        return getReviewSimpleResCursorResult(reviewSimpleRes, reviewPosts);
    }

    @Transactional
    public CursorResult<ReviewSimpleRes> getSubscribeReview(Member subscriber, int page) {

        if (!subscribeRepository.existsBySubscriber(subscriber)) {
            return new CursorResult<>(
                    new ArrayList<>(1),
                    (long)page,
                    false
            );
        }

        CursorResult<ReviewPost> subscribeReviewList = reviewPostRepository.findSubscribeReviewList(subscriber, PageRequest.of(page-1, 10));

        ArrayList<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        for (ReviewPost value : subscribeReviewList.values()) {
            String profilePhotoPath = value.getMember().getProfilePhoto() != null ?
                    value.getMember().getProfilePhoto().getS3Path() :
                    null;

            List<PhotoInPost> photoS3Path = photoService.getPhotoS3Path(value);
            String bodyPhoto = photoS3Path.isEmpty() ? null : photoS3Path.get(0).photoPath();

            List<HashtagRes> hashtags = new ArrayList<>();
            for(Tuple tuple : hashtagClassificationService.getHashtags(value.getId())) {
                hashtags.add(new HashtagRes(
                        tuple.get(0, Long.class),
                        tuple.get(1, String.class)
                ));
            }
            reviewSimpleRes.add(new ReviewSimpleRes(
                    value.getId(),
                    value.getBody(),
                    profilePhotoPath,
                    value.getMember().getNickName(),
                    bodyPhoto,
                    hashtags
            ));
        }

        return new CursorResult<>(
                reviewSimpleRes,
                (long) page +1,
                subscribeReviewList.hasNext()
        );
    }

    private Page<ReviewPost> getMyReviewPosts(Long memberId, Long cursorId, String sort, Pageable page) {
        return cursorId == null ?
                reviewPostRepository.findMyPostFirstPageByMemberId(memberId, sort, page) :
                reviewPostRepository.findByMemberIdLessThanOrderByIdDesc(memberId, cursorId, sort, page);
    }
}
