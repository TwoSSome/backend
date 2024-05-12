package towssome.server.service;

import lombok.RequiredArgsConstructor;
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
    private final ReviewPostRepository reviewPostRepository;
    private final PhotoService photoService;
    private final ReviewPostService reviewPostService;

    /** 조회 기록 저장(최초 조회 시) */
    public void viewProcess(ReviewPost review, Member member) {

        ViewLike viewLike = new ViewLike(
                review,
                member,
                true,
                false
        );
        viewLikeRepository.save(viewLike);
    }

    /** 좋아요/좋아요 취소 */
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
            } else { // 좋아요를 누르지 않은 경우
                viewLike.setLike();
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

    /** ----------------- 좋아요 조회 ----------------- */
    public CursorResult<ReviewPostRes> getLike(Member member, Long cursorId, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final List<ReviewPost> reviewPosts = getLikePosts(member.getId(), cursorId, page);
        return getReviewPostResCursorResult(member, reviewPostRes, reviewPosts);
    }

    /** ----------------- 최근 조회 기록 조회 ----------------- */
    public CursorResult<ReviewPostRes> getRecentView(Member member, Long cursorId, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final List<ReviewPost> reviewPosts = getRecentViewPosts(member.getId(), cursorId, page);
        return getReviewPostResCursorResult(member, reviewPostRes, reviewPosts);
    }

    /**Entity를 DTO로 변환 -> 다음 페이지가 존재하는지 확인 */
    public CursorResult<ReviewPostRes> getReviewPostResCursorResult(Member member, List<ReviewPostRes> reviewPostRes, List<ReviewPost> reviewPosts) {
        for(ReviewPost review : reviewPosts) {
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLatsModifiedDate(),
                    member.getId(),
                    photoService.getPhotoS3Path(review),
                    reviewPostService.isMyPost(member, review),
                    isLikedPost(member, review),
                    isBookmarkedPost(member, review)));
        }
        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPostRes, hasNext(lastIdOfList));
    }


    /**cursorId보다 작은페이지(다음페이지)에서 좋아요 누른 리뷰글만 불러옴*/
    private List<ReviewPost> getLikePosts(Long memberId, Long cursorId, Pageable page) {
            return cursorId == null ?
                    viewLikeRepository.findLikeByMemberIdOrderByIdDesc(memberId, page) :
                    viewLikeRepository.findLikeByIdAndMemberIdLessThanOrderByIdDesc(cursorId, memberId, page);
    }

    /**cursorId보다 작은페이지(다음페이지)에서 최근 조회한 리뷰글만 불러옴*/
    private List<ReviewPost> getRecentViewPosts(Long memberId, Long cursorId, Pageable page) {
        return cursorId == null ?
                viewLikeRepository.findAllByMemberIdOrderByIdDesc(memberId, page) :
                viewLikeRepository.findByMemberIdLessThanOrderByIdDesc(cursorId, memberId, page);
    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return !this.reviewPostRepository.findByIdLessThanOrderByIdDesc(id, Pageable.ofSize(1)).isEmpty();
    }
}
