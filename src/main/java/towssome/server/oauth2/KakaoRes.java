package towssome.server.oauth2;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoRes implements OAuth2Res{

    private final Map<String, Object> attribute;

    public KakaoRes(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Object account = attribute.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap) account;
        return accountMap.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
