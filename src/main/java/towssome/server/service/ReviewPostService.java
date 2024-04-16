package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReviewPostDTO;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.ReviewPostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;

    public void createReview(ReviewPostDTO reviewPostDTO) { // 사진 추가 필요
        ReviewPost reviewPost = new ReviewPost(
                reviewPostDTO.body(),
                reviewPostDTO.price(),
                reviewPostDTO.member()
        );
        reviewPostRepository.save(reviewPost);
    }

    public ReviewPostDTO getReview(Long reviewId) {
        ReviewPost reviewPost = reviewPostRepository.findById(reviewId).orElseThrow();
        return new ReviewPostDTO(
                reviewPost.getBody(),
                reviewPost.getPrice(),
                reviewPost.getMember()
        );
    }

    public CursorResult<ReviewPost> getReviewPage(Long cursorId, Pageable page) {
        final List<ReviewPost> reviewPosts = getReviewPosts(cursorId, page);
        final Long lastIdOfList = reviewPosts.isEmpty() ?
                null : reviewPosts.get(reviewPosts.size() - 1).getId();

        return new CursorResult<>(reviewPosts, hasNext(lastIdOfList));
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
}
