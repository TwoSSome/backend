package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CursorResult;
import towssome.server.dto.PhotoInPost;
import towssome.server.dto.ReviewSimpleRes;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashtagClassificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationService{

    private final HashtagClassificationRepository hashtagClassificationRepository;
    private final PhotoService photoService;

    public CursorResult<ReviewSimpleRes> getReviewPageByHashtag(String keyword, Long cursorId, String sort, Pageable page) {
        List<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getReviewPostByHashtag(keyword, cursorId, sort, page);
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

            reviewSimpleRes.add(new ReviewSimpleRes(
                    review.getId(),
                    review.getBody(),
                    profilePhoto,
                    review.getMember().getNickName(),
                    bodyPhoto,
                    getHashtags(review.getId())
            ));
        }
        cursorId = reviewPosts.isEmpty() ?
                null : reviewPosts.getContent().get(reviewPosts.getContent().size() - 1).getId();
        return new CursorResult<>(reviewSimpleRes, cursorId, reviewPosts.hasNext());
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
