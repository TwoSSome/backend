package towssome.server.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.*;
import towssome.server.exception.DuplicateIdException;
import towssome.server.repository.CategoryRepository;
import towssome.server.repository.HashTagRepository;
import towssome.server.repository.MemberRepository;
import towssome.server.repository.ProfileTagRepository;
import towssome.server.service.MailSendService;
import towssome.server.service.PhotoService;

import java.io.IOException;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CategoryRepository categoryRepository;
    private final PhotoService photoService;
    private final HashTagRepository hashTagRepository;
    private final ProfileTagRepository profileTagRepository;
    private final MailSendService mailSendService;
    @Value("${email.verification.required}")
    private boolean emailVerification;

    public int sendEmailVerification(String email) {
        if (memberRepository.existsByUsername(email)) {
            throw new DuplicateIdException("이미 가입되어 있는 이메일 입니다");
        }
        return mailSendService.joinEmail(email);
    }

    public boolean verificationEmail(String email,int authNum) {
       return mailSendService.CheckAuthNum(email, authNum);
    }

    public Member joinProcess(JoinDTO joinDTO, MultipartFile multipartFile)  {

        String username = joinDTO.username();

        String password = joinDTO.password();
        String nickname = joinDTO.nickname();

        Boolean isExist = memberRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateIdException("중복된 아이디입니다");
        }

        Photo profilePhoto = null;

        try {
            profilePhoto = photoService.saveProfilePhoto(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Member member = new Member(
                username,
                bCryptPasswordEncoder.encode(password),
                nickname,
                0,
                profilePhoto, // 프로필 사진 설정을 가능케 할 것인가?
                "ROLE_USER"
        );

        memberRepository.save(member);

        //마스터 카테고리 생성
        Category category = new Category(
                "북마크",
                member
        );
        categoryRepository.save(category);

        //프로필태그 생성
        ArrayList<HashTag> hashTags = new ArrayList<>();
        for (Long profileTagId : joinDTO.profileTagIds()) {
            hashTags.add(hashTagRepository.findById(profileTagId).orElseThrow());
        }

        for (HashTag hashTag : hashTags) {
            profileTagRepository.save(new ProfileTag(
                    member,
                    hashTag
            ));
        }

        return member;
    }


}
