package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ViewLikeReq;
import towssome.server.entity.BookMark;
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
    private final MemberRepository memberRepository;
    private final ReviewPostRepository reviewPostRepository;

    /** 조회 기록 저장(최초 조회 시) */
    public void viewProcess(ViewLikeReq req) {
        ViewLike viewLike = new ViewLike(
                reviewPostRepository.findById(req.reviewId()).orElseThrow(),
                memberRepository.findById(req.memberId()).orElseThrow(),
                true,
                false
        );
        viewLikeRepository.save(viewLike);
    }

    /** 좋아요 처리 */
    public void likeProcess(ViewLikeReq req) {
        Member member = memberRepository.findById(req.memberId()).orElseThrow();
        ViewLike viewLike = viewLikeRepository.findByReviewPostIdAndMemberId(req.reviewId(), req.memberId());
        if (viewLike == null) {
            viewLike = new ViewLike(
                    reviewPostRepository.findById(req.reviewId()).orElseThrow(),
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

    /** ----------------- 조회 기록 및 좋아요 조회 ----------------- */
    public CursorResult<ReviewPost> getLike(Long memberId, Long cursorId, Pageable page) {
        final List<ReviewPost> reviewPosts = getLikePosts(memberId, cursorId, page);
        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPosts, hasNext(lastIdOfList));
    }

    public CursorResult<ReviewPost> getRecentView(Long memberId, Long cursorId, Pageable page) {
        final List<ReviewPost> reviewPosts = getRecentViewPosts(memberId, cursorId, page);
        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPosts, hasNext(lastIdOfList));
    }

    private List<ReviewPost> getLikePosts(Long memberId, Long cursorId, Pageable page) {
            return cursorId == null ?
                    viewLikeRepository.findLikeByMemberIdOrderByIdDesc(memberId, page) :
                    viewLikeRepository.findLikeByIdAndMemberIdLessThanOrderByIdDesc(cursorId, memberId, page);
    }

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
