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
import org.springframework.web.client.RestTemplate;
import towssome.server.entity.HashTag;
import towssome.server.entity.ReviewPost;
import towssome.server.repository.HashTagRepository;

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

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

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

    @Transactional
    public void deleteHashtag(Long reviewId, Long hashtagId) {
        hashTagRepository.deleteHashtag(reviewId, hashtagId);
    }

    @Transactional
    public void deleteAllHashtags(Long reviewId) {
        hashTagRepository.deleteAllHashtags(reviewId);
    }

    public List<String> getHashtags(Long reviewId) {
        return hashTagRepository.findHashtags(reviewId);
    }
}


