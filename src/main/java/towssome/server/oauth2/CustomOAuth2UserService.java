package towssome.server.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.entity.Member;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.MemberRepository;

import static towssome.server.advice.RoleAdvice.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    //리소스 서버에서 제공되는 유저 데이터 받음
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Res oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverRes(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleRes(oAuth2User.getAttributes());

        } else {
            return null;
        }

        //로그인 완료 후 진행 로직

        log.info("oAuth2Response.getProvider() = {}",oAuth2Response.getProvider());
        log.info("oAuth2Response.getProviderId() = {}",oAuth2Response.getProviderId());
        String socialId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        log.info("email = {}",email);

        boolean isExistMember = memberRepository.existsByEmail(email);

        if (!isExistMember) {

            //가입되어 있지 않은 유저일 경우
            Member member = new Member(
                    "",
                    "",
                    "",
                    0,
                    null,
                    ROLE_TEMP,
                    email
            );
            member.setSocialId(socialId);

            memberRepository.save(member);

            return new CustomOAuth2User(member);

        } else {

            Member member = null;
            try {
                member = memberRepository.findBySocialId(socialId)
                        .orElseThrow(() -> new NotFoundMemberException("이 소셜id를 가진 멤버 없음"));
            } catch (NotFoundMemberException e) {
                member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new NotFoundMemberException("이 이메일을 가진 멤버 없음"));
                member.setSocialId(socialId);
            }

            //가입된 유저의 경우

            UserDTO userDTO = new UserDTO(
                    member.getRole(),
                    member.getNickName(),
                    member.getEmail(),
                    member.getUsername()
            );

            return new CustomOAuth2User(member);

        }


    }

}
