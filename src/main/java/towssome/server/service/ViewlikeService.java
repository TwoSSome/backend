package towssome.server.service;

import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.dto.HashtagRes;
import towssome.server.dto.PhotoInPost;
import towssome.server.dto.ReviewSimpleRes;
import towssome.server.entity.BookMark;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;
import towssome.server.repository.BookMarkRepository;
import towssome.server.repository.ViewLikeRepository;
import towssome.server.repository.ViewLikeRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewlikeService {

    private final BookMarkRepository bookMarkRepository;
    private final ViewLikeRepository viewLikeRepository;
    private final PhotoService photoService;
    private final HashtagClassificationService hashtagClassificationService;
    private final ViewLikeRepositoryCustom viewLikeRepositoryCustom;

    /** 조회 기록 저장(최초 조회 시) */
    @Transactional
    public void viewProcess(ReviewPost review, Member member) {
        ViewLike viewLike = viewLikeRepository.findByReviewPostAndMember(review, member);
        if (viewLike == null) {
            viewLike = new ViewLike(
                    review,
                    member,
                    true,
                    false
            );
            viewLikeRepository.save(viewLike);
            review.getMember().addRankPoint(1);//랭크포인트 추가
        } else {
            viewLike = viewLikeRepository.findByReviewPostAndMember(review, member);
            viewLike.addViewAmount(); //조회수 추가
            review.getMember().addRankPoint(1); //랭크포인트 추가
        }


    }

    /** 좋아요 or 좋아요 취소 */
    @Transactional
    public void likeProcess(ReviewPost review, Member member) {
        ViewLike viewLike = viewLikeRepository.findByReviewPostIdAndMemberId(review.getId(), member.getId());
        if (viewLike == null) {
            viewLike = new ViewLike(
                    review,
                    member,
                    true,
                    true
            );
            review.getMember().addRankPoint(10);
            viewLikeRepository.save(viewLike);
        } else {
            if (viewLike.getLikeFlag()) { // 이미 좋아요를 누른 경우
                viewLike.setUnlike();
                review.getMember().addRankPoint(-10);
            } else { // 좋아요를 누르지 않은 경우
                viewLike.setLike();
                review.getMember().addRankPoint(10);
            }
        }
    }

    //좋아요 여부
    public boolean isLikedPost(Member member, ReviewPost reviewPost) {
        ViewLike viewLike = viewLikeRepository.findByReviewPostAndMember(reviewPost, member);
        return viewLike.getLikeFlag();
    }

    //북마크 여부
    public boolean isBookmarkedPost(Member member, ReviewPost reviewPost) {
        List<BookMark> list = bookMarkRepository.findAllBookmarkByMember(member);
        return list.contains(reviewPost);
    }

    /** ----------------- 자신의 좋아요 기록 조회 ----------------- */
    public CursorResult<ReviewSimpleRes> getLike(Member member, Long pageId, String sort, int size) {
        if (pageId == null) {
            pageId = 0L;
        } else if (pageId < 0) {
            throw new IllegalArgumentException("pageId는 0 이상이어야 합니다.");
        } else if (pageId > 0) {
            pageId = pageId - 1;
        }
        final CursorResult<ReviewPost> reviewPosts = getLikePosts(member.getId(), pageId, sort, size);
        return getReviewSimpleResCursorResult(member, reviewPosts);
    }

    /**cursorId보다 작은페이지(다음페이지)에서 좋아요 누른 리뷰글만 불러옴*/
    private CursorResult<ReviewPost> getLikePosts(Long memberId, Long pageId, String sort, int size) {
        return viewLikeRepositoryCustom.findLikeByMemberIdOrderByIdDesc(memberId, pageId, sort, size);
    }


    /** ----------------- 자신의 최근 조회 기록 조회 ----------------- */
    public CursorResult<ReviewSimpleRes> getRecentView(Member member, Long pageId, String sort, int size) {
        if (pageId == null) {
            pageId = 0L;
        } else if (pageId < 0) {
            throw new IllegalArgumentException("pageId는 0 이상이어야 합니다.");
        } else if (pageId > 0) {
            pageId = pageId - 1;
        }
        final CursorResult<ReviewPost> reviewPosts = getRecentViewPosts(member.getId(), pageId, sort, size);
        return getReviewSimpleResCursorResult(member, reviewPosts);
    }

    /**최근 조회한 리뷰글만 불러옴*/
    private CursorResult<ReviewPost> getRecentViewPosts(Long memberId, Long pageId, String sort, int size) {
        return viewLikeRepositoryCustom.findRecentByMemberIdOrderByIdDesc(memberId, pageId, sort, size);
    }



    /** 반환 함수 */
    private CursorResult<ReviewSimpleRes> getReviewSimpleResCursorResult(Member member, CursorResult<ReviewPost> result) {
        List<ReviewSimpleRes> reviewPostRes = new ArrayList<>();
        for(ReviewPost review : result.values()) {
            List<PhotoInPost> bodyPhotos = photoService.getPhotoS3Path(review);
            String bodyPhoto = bodyPhotos.isEmpty() ? null : bodyPhotos.get(0).photoPath();

            String profilePhoto = member.getProfilePhoto() != null ?
                    member.getProfilePhoto().getS3Path() :
                    null;

            List<HashtagRes> hashtags = new ArrayList<>();
            for(Tuple tuple : hashtagClassificationService.getHashtags(review.getId())) {
                hashtags.add(new HashtagRes(
                        tuple.get(0, Long.class),
                        tuple.get(1, String.class)
                ));
            }
            reviewPostRes.add(new ReviewSimpleRes(
                    review.getId(),
                    review.getBody(),
                    profilePhoto,
                    member.getNickName(),
                    bodyPhoto,
                    hashtags
            ));
        }
        return new CursorResult<>(reviewPostRes, result.nextPageId(), result.hasNext());
    }
}
