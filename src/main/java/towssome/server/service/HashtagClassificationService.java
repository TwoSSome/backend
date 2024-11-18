package towssome.server.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.advice.MemberAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.HashtagRes;
import towssome.server.dto.PhotoInPost;
import towssome.server.dto.ReviewSimpleRes;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.hashtag_classification.HashtagClassificationRepository;
import towssome.server.repository.viewlike.ViewLikeRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationService{
    private final HashtagClassificationRepository hashtagClassificationRepository;
    private final PhotoAdvice photoAdvice;
    private final ViewLikeRepositoryCustom viewLikeRepositoryCustom;
    private final SearchHistoryService searchHistoryService;
    private final MemberAdvice memberAdvice;

    public CursorResult<ReviewSimpleRes> getReviewPageByHashtag(String keyword, Long cursorId, String sort, Pageable page) {
        List<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        final Page<ReviewPost> reviewPosts = getReviewPostByHashtag(keyword, cursorId, sort, page);
        return getReviewSimpleResCursorResult(reviewSimpleRes, reviewPosts);
    }

    private CursorResult<ReviewSimpleRes> getReviewSimpleResCursorResult(List<ReviewSimpleRes> reviewSimpleRes,
                                                                         Page<ReviewPost> reviewPosts) {
        Long cursorId;
        for(ReviewPost review : reviewPosts) {
            List<PhotoInPost> bodyPhotos = photoAdvice.getPhotoS3Path(review);
            String bodyPhoto = bodyPhotos.isEmpty() ? null : bodyPhotos.get(0).photoPath();
            String profilePhoto = review.getMember().getProfilePhoto() != null ?
                    review.getMember().getProfilePhoto().getS3Path() :
                    null;

            List<HashtagRes> hashtags = new ArrayList<>();
            for(Tuple tuple : getHashtags(review.getId())) {
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
                    review.getReviewType(),
                    hashtags,
                    viewLikeRepositoryCustom.findLikeAmountByReviewPost(review.getId())
            ));
        }
        cursorId = reviewPosts.isEmpty() ?
                null : reviewPosts.getContent().get(reviewPosts.getContent().size() - 1).getId();
        return new CursorResult<>(reviewSimpleRes, cursorId, reviewPosts.hasNext());
    }

    public Page<ReviewPost> getReviewPostByHashtag(String keyword, Long cursorId, String sort, Pageable page){
        if (cursorId == null){
            Member member = memberAdvice.findJwtMember();
            if (member != null) searchHistoryService.updateSearchHistory(member, keyword);
            return hashtagClassificationRepository.findFirstReviewPageByHashtag(keyword, sort, page);
        }
        else {
            return hashtagClassificationRepository.findReviewPageByCursorIdAndHashTag(keyword, cursorId, sort, page);
        }

    }

    public List<Tuple> getHashtags(Long reviewId) {
        return hashtagClassificationRepository.findHashtagsByReviewId(reviewId);
    }
}
