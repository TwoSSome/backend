package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashtagClassificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationService {

    private final HashtagClassificationRepository hashtagClassificationRepository;

    // 해시태그로 리뷰글 검색
    public Page<ReviewPost> getReviewPostByHashtag(String keyword, String sort, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return hashtagClassificationRepository.findReviewsByHashtagOrderBySort(keyword,sort, pageable);
    }

    public List<String> getHashtags(Long reviewId) {
        return hashtagClassificationRepository.findHashtagsByReviewId(reviewId);
    }

    public Object getAllHashtags() {
        return hashtagClassificationRepository.findAll();
    }
}
