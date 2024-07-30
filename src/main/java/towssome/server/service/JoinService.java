package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.*;
import towssome.server.exception.DuplicateIdException;
import towssome.server.jwt.JoinDTO;
import towssome.server.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public int sendEmailVerification(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateIdException("이미 가입되어 있는 이메일 입니다");
        }
        if (emailVerificationRepository.existsByEmail(email)) {
            emailVerificationRepository.deleteByEmail(email);
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
        String email = joinDTO.email();

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
                "ROLE_USER",
                email
        );

        memberRepository.save(member);

        //마스터 카테고리 생성
        Category category = new Category(
                "북마크",
                member
        );
        categoryRepository.save(category);

        //프로필태그 생성
        profileTageSave(joinDTO.profileTagNames(),member);

        return member;
    }

    private void profileTageSave(List<String> list, Member member) {

        ArrayList<HashTag> hashTags = new ArrayList<>();

        for (String name : list) {
            if (hashTagRepository.existsByName(name)) {
                hashTags.add(hashTagRepository.findByName(name));
            }else{
                HashTag save = hashTagRepository.save(new HashTag(
                        name,
                        0L
                ));
                hashTags.add(save);
            }
        }

        for (HashTag hashTag : hashTags) {
            profileTagRepository.save(new ProfileTag(
                    member,
                    hashTag
            ));
        }

    }


}
