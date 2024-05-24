package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReviewPostRes;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashtagClassificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationService {

    private final HashtagClassificationRepository hashtagClassificationRepository;
    private final PhotoService photoService;

    public CursorResult<ReviewPostRes> getReviewPageByHashtag(String keyword, Long cursorId, String sort, Pageable page) {
        List<ReviewPostRes> reviewPostRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getReviewPostByHashtag(keyword, cursorId, sort, page);
        for(ReviewPost review : reviewPosts){
            reviewPostRes.add(new ReviewPostRes(
                    review.getBody(),
                    review.getPrice(),
                    review.getCreateDate(),
                    review.getLastModifiedDate(),
                    review.getMember().getId(),
                    photoService.getPhotoS3Path(review),
                    false,
                    false,
                    false,
                    getHashtags(review.getId()),
                    review.getReviewType(),
                    review.getStarPoint(),
                    review.getWhereBuy()
            ));
        }
        cursorId = reviewPosts.isEmpty()?
                null:reviewPosts.getContent().get(reviewPosts.getContent().size()-1).getId();
        return new CursorResult<>(reviewPostRes, cursorId, reviewPosts.hasNext());
    }


    // 해시태그로 리뷰글 검색
    public Page<ReviewPost> getReviewPostByHashtag(String keyword, Long cursorId, String sort, Pageable page){
        return cursorId == null ?
                hashtagClassificationRepository.findFirstReviewPageByHashtag(keyword, sort, page):
                hashtagClassificationRepository.findReviewPageByCursorIdAndHashTag(keyword, cursorId, sort, page);
    }

    public List<String> getHashtags(Long reviewId) {
        return hashtagClassificationRepository.findHashtagsByReviewId(reviewId);
    }

    public Object getAllHashtags() {
        return hashtagClassificationRepository.findAll();
    }
}
