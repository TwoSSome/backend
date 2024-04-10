package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.ReviewPostDTO;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.ReviewPostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;

    public List<ReviewPost> getReviewList() {
        return reviewPostRepository.findAll();
    }
    public void createReview(ReviewPostDTO reviewPostDTO) {
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
}
