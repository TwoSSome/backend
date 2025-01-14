package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MailSendAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.advice.ServiceAdvice;
import towssome.server.dto.RankerRes;
import towssome.server.entity.*;
import towssome.server.enumrated.EmailType;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.jwt.JwtStatic;
import towssome.server.jwt.JwtUtil;
import towssome.server.repository.*;
import towssome.server.repository.member.MemberRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PhotoAdvice photoAdvice;
    private final ProfileTagRepository profileTagRepository;
    private final ServiceAdvice serviceAdvice;
    private final MailSendAdvice mailSendAdvice;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    /**
     * 프로필 사진 변경
     * @param member
     * @param photo
     */
    @Transactional
    public void changeProfilePhoto(Member member, MultipartFile photo) {
        Photo previousPhoto = member.getProfilePhoto();

        Photo photo1 = null;
        try {
            photo1 = photoAdvice.saveProfilePhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (previousPhoto != null) {
            photoAdvice.deletePhoto(previousPhoto.getId());
        }
        member.changeProfilePhoto(photo1);
    }

    /**
     * 프로필 변경
     * @param username
     * @param nickName
     * @param tagList
     */
    @Transactional
    public void changeProfile(String username, String nickName, List<String> tagList) {

        Member member = memberRepository.findByUsername(username).orElseThrow();

        member.changeProfile(nickName);
        profileTagRepository.deleteAllByMember(member);

        serviceAdvice.storeHashtag(tagList,member);

    }

    /**
     * 해당 멤버의 프로필 태그 리턴
     * @param member
     * @return
     */
    @Transactional
    public List<HashTag> getProfileTag(Member member) {
        ArrayList<HashTag> result = new ArrayList<>();

        List<ProfileTag> list = profileTagRepository.findAllByMember(member);
        for (ProfileTag profileTag : list) {
            result.add(profileTag.getHashTag());
        }

        return result;
    }

    /**
     * 프로필 태그들을 String 형식으로 리턴
     * @param member
     * @return
     */
    @Transactional
    public List<String> getProfileTagString(Member member) {
        ArrayList<String > result = new ArrayList<>();

        List<ProfileTag> list = profileTagRepository.findAllByMember(member);
        for (ProfileTag profileTag : list) {
            result.add(profileTag.getHashTag().getName());
        }

        return result;
    }

    /**
     * 랭킹에 있는 멤버 프로필 10개 리턴
     */
    @Transactional
    public List<RankerRes> getRanker() {
        List<Member> ranker = memberRepository.findRanker(10);
        ArrayList<RankerRes> profileRes = new ArrayList<>();
        int rank = 1;
        for (Member member : ranker) {
            profileRes.add(new RankerRes(
                    member.getId(),
                    member.getNickName(),
                    member.getProfilePhoto() != null ? member.getProfilePhoto().getS3Path() : null,
                    getProfileTagString(member),
                    member.getRankPoint(),
                    rank++
            ));
        }
        return profileRes;
    }

    public boolean dupIdCheck(String username) {
        return !memberRepository.existsByUsername(username);
    }

    public void findMyUsername(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundMemberException("해당 이메일을 가진 멤버가 없습니다.")
        );
        mailSendAdvice.sendFindUsername(email,member.getUsername());
    }

    public void reconfigPasswordRequest(String email, String username) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundMemberException("해당 이메일을 가진 멤버가 없습니다.")
        );
        if (member.getUsername().equals(username)) {
            mailSendAdvice.sendReconfigPassword(email);
        }else
            throw new NotFoundMemberException("이메일과 아이디가 일치하지 않습니다");
    }

    public boolean checkReconfigPasswordAuthNum(int authNum, String email) {
        return mailSendAdvice.CheckAuthNum(email, authNum, EmailType.RECONFIG_PASSWORD);
    }

    public String getJwtForReconfigPassword(String email) {
        return jwtUtil.createJwt("reconfig", email, JwtStatic.RECONFIG_EXPIRE_MS);
    }

    @Transactional
    public void reconfigPassword(String password, String email) {
        memberRepository.reconfigPassword(encoder.encode(password),email);
    }

    // -----deprecated-----
    // 종설 4 백엔드 페이지 7
//    /**
//     * 가상 연인 프로필 태그 생성
//     * @param hashtags
//     * @param member
//     */
//    @Transactional
//    public void createVirtual(List<String> hashtags, Member member, MultipartFile file, String mateName) {
//
//        Photo photo = photoService.saveVirtualPhoto(file);
//        member.changeVirtualPhoto(photo);
//        member.changeVirtualMateName(mateName);
//
//        for (String hashtag : hashtags) {
//            if (hashTagRepository.existsByName(hashtag)) {
//                HashTag hashTag = hashTagRepository.findHashTagByName(hashtag).orElseThrow();
//                virtualMateHashtagRepository.save(new VirtualMateHashtag(
//                        member,
//                        hashTag
//                ));
//            }else{
//                HashTag save = hashTagRepository.save(new HashTag(
//                        hashtag,
//                        0L
//                ));
//                virtualMateHashtagRepository.save(new VirtualMateHashtag(
//                        member,
//                        save
//                ));
//            }
//        }
//
//    }
//
//    /**
//     * 가상 연인 프로필 태그 가져오기
//     * @param member
//     * @return
//     */
//    @Transactional
//    public List<HashTag> getVirtualTag(Member member) {
//        List<VirtualMateHashtag> list = virtualMateHashtagRepository.findAllByMember(member);
//        ArrayList<HashTag> hashTags = new ArrayList<>();
//        for (VirtualMateHashtag v : list) {
//            hashTags.add(v.getHashTag());
//        }
//        return hashTags;
//    }
//
}
