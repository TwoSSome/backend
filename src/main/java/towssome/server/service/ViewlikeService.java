package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.entity.BookMark;
import towssome.server.dto.ReviewPostRes;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;
import towssome.server.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewlikeService {

    private final BookMarkRepository bookMarkRepository;
    private final ViewLikeRepository viewLikeRepository;
    private final PhotoService photoService;
    private final ReviewPostService reviewPostService;
    private final HashtagClassificationService hashtagClassificationService;
    private final ViewLikeRepositoryCustom viewLikeRepositoryCustom;

    /** 조회 기록 저장(최초 조회 시) */
    @Transactional
    public void viewProcess(ReviewPost review, Member member) {

        if (!viewLikeRepository.existsByMemberAndReviewPost(member, review)) {
            ViewLike viewLike = new ViewLike(
                    review,
                    member,
                    true,
                    false
            );
            viewLikeRepository.save(viewLike);
            review.getMember().addRankPoint(1);//랭크포인트 추가
        } else {
            ViewLike viewlike = viewLikeRepository.findByReviewPostAndMember(review, member);
            viewlike.addViewAmount(); //조회수 추가
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
        } else {
            if (viewLike.getLikeFlag()) { // 이미 좋아요를 누른 경우
                viewLike.setUnlike();
                review.getMember().addRankPoint(-10);
            } else { // 좋아요를 누르지 않은 경우
                viewLike.setLike();
                review.getMember().addRankPoint(10);
            }
        }
        viewLikeRepository.save(viewLike);
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
    public CursorResult<ReviewPostRes> getLike(Member member, Long cursorId, String sort, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getLikePosts(member.getId(), cursorId, sort, page);
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    review.getMember().getId(),
                    photoService.getPhotoS3Path(review),
                    reviewPostService.isMyPost(member, review),
                    true,
                    isBookmarkedPost(member, review),
                    hashtagClassificationService.getHashtags(review.getId()),
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy()
            ));
        }
        cursorId = reviewPosts.isEmpty() ?
                null : reviewPosts.getContent().get(reviewPosts.getContent().size() - 1).getId();
        return new CursorResult<>(reviewPostRes, cursorId, reviewPosts.hasNext());
    }

    /** ----------------- 자신의 최근 조회 기록 조회 ----------------- */
    public CursorResult<ReviewPostRes> getRecentView(Member member, Long cursorId, String sort, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getRecentViewPosts(member.getId(), cursorId, sort, page);
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    review.getMember().getId(),
                    photoService.getPhotoS3Path(review),
                    reviewPostService.isMyPost(member, review),
                    isLikedPost(member, review),
                    isBookmarkedPost(member, review),
                    hashtagClassificationService.getHashtags(review.getId()),
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy()
            ));
        }
        cursorId = reviewPosts.isEmpty() ?
                null : reviewPosts.getContent().get(reviewPosts.getContent().size() - 1).getId();
        return new CursorResult<>(reviewPostRes, cursorId, reviewPosts.hasNext());
    }

    /**cursorId보다 작은페이지(다음페이지)에서 좋아요 누른 리뷰글만 불러옴*/
    private Page<ReviewPost> getLikePosts(Long memberId, Long cursorId, String sort, Pageable page) {
            return cursorId == null ?
                    viewLikeRepositoryCustom.findLikeByMemberIdOrderByIdDesc(memberId, sort, page) :
                    viewLikeRepositoryCustom.findLikeByIdAndMemberIdLessThanOrderByIdDesc(cursorId, memberId, sort, page);
    }

    /**cursorId보다 작은페이지(다음페이지)에서 최근 조회한 리뷰글만 불러옴*/
    private Page<ReviewPost> getRecentViewPosts(Long memberId, Long cursorId, String sort, Pageable page) {
        return cursorId == null ?
                viewLikeRepositoryCustom.findRecentByMemberIdOrderByIdDesc(memberId, sort, page) :
                viewLikeRepositoryCustom.findRecentByMemberIdLessThanOrderByIdDesc(cursorId, memberId, sort, page);
    }
}
