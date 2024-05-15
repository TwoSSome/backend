package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import towssome.server.entity.HashTag;
import towssome.server.entity.HashtagClassification;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashTagRepository;
import towssome.server.repository.HashtagClassificationRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashtagService {
    @Autowired
    private RestTemplate restTemplate;
    private final HashTagRepository hashTagRepository;

    public void createHashtag(ReviewPost reviewPost, String body) {
        String url = "http://localhost:5000/makeHashtag";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));  // 문자열 전송을 위한 컨텐트 타입

    private final HashTagRepository hashtagRepository;
    private final HashtagClassificationRepository hashtagClassificationRepository;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

    /**
     * koBert 를 통해 추출한 문자열을 인자로 받습니다. 해당 문자열이 해시태그로 저장되어 있으면 1증가
     * 없으면 해시태그를 count 를 1로 설정하고 새로 생성합니다
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
        System.out.println("Response from Python: " + response.getBody());
        String jsonResponse =  decodeUnicode(response.getBody());

        // JSON 문자열을 JSONObject로 변환
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // "hashtags" 키에 해당하는 JSONArray 가져오기
        JSONArray hashtagsArray = jsonObject.getJSONArray("hashtags");

        // JSONArray를 Java의 List<String>으로 변환
        List<String> hashtagsList = new ArrayList<>();
        for (int i = 0; i < hashtagsArray.length(); i++) {
            hashtagsList.add(hashtagsArray.getString(i));
        }
        for(String hashtag : hashtagsList) {
            HashTag hashTag = new HashTag(reviewPost, hashtag);
            hashTagRepository.save(hashTag);
        }
    }

        for (HashTag hashTag : forReviewPostHashtag) {
            hashtagClassificationRepository.save(new HashtagClassification(
                    hashTag,
                    reviewPost
            ));
    public String decodeUnicode(String escapedStr) {
        Pattern pattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(escapedStr);
        StringBuilder sb = new StringBuilder(escapedStr.length());
        while (matcher.find()) {
            String group = matcher.group(1);
            char c = (char) Integer.parseInt(group, 16);
            matcher.appendReplacement(sb, Character.toString(c));
        }
        matcher.appendTail(sb);
        return sb.toString();
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
