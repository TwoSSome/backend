package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.entity.HashTag;
import towssome.server.entity.HashtagClassification;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashTagRepository;
import towssome.server.repository.HashtagClassificationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashTagRepository hashtagRepository;
    private final HashtagClassificationRepository hashtagClassificationRepository;

    /**
     * koBert 를 통해 추출한 문자열을 인자로 받습니다. 해당 문자열이 해시태그로 저장되어 있으면 1증가
     * 없으면 해시태그를 새로 생성합니다
     * @param list
     * @param reviewPost
     */
    public void createHashtag(List<String> list, ReviewPost reviewPost){
        ArrayList<HashTag> forReviewPostHashtag = new ArrayList<>();
        for (String s : list) {
            if(!hashtagRepository.existsByName(s)){
                HashTag save = hashtagRepository.save(new HashTag(
                        s,
                        1L
                ));
                forReviewPostHashtag.add(save);
            }else {
                HashTag find = hashtagRepository.findByName(s);
                find.setCount(find.getCount()+1);
                forReviewPostHashtag.add(find);
            }
        }

        for (HashTag hashTag : forReviewPostHashtag) {
            hashtagClassificationRepository.save(new HashtagClassification(
                    hashTag,
                    reviewPost
            ));
        }
    }

    /**
     * 리뷰글에 담긴 해시태그를 삭제합니다. 해시태그와 리뷰글의 다대다 테이블의 컬럼도 삭제합니다
     * 해시태그 테이블은 실제로 삭제되는게 아니라 0이 되어도 다시 사용될 여지가 있기에 count 만 줄입니다.
     * @param reviewPost
     */
    @Transactional
    public void deleteHashtagByReviewPost(ReviewPost reviewPost){
        List<HashtagClassification> byReviewPost = hashtagClassificationRepository.findAllByReviewPost(reviewPost);
        for (HashtagClassification hashtagClassification : byReviewPost) {
            HashTag hashTag = hashtagClassification.getHashTag();
            hashTag.setCount(hashTag.getCount()-1);
        }
        hashtagClassificationRepository.deleteAll(byReviewPost);
    }

    /**
     * 리뷰글에 담긴 해시태그를 찾아 반환합니다
     * @param reviewPost
     * @return
     */
    @Transactional
    public List<HashTag> findAllByReviewPost(ReviewPost reviewPost){
        List<HashtagClassification> allByReviewPost = hashtagClassificationRepository.findAllByReviewPost(reviewPost);
        List<HashTag> result = new ArrayList<>();
        for (HashtagClassification hashtagClassification : allByReviewPost) {
            result.add(hashtagClassification.getHashTag());
        }
        return result;
    }

}
