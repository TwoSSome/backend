package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.*;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PhotoService photoService;
    private final VirtualMateHashtagRepository virtualMateHashtagRepository;
    private final HashTagRepository hashTagRepository;
    private final ProfileTagRepository profileTagRepository;

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    /**
     * 가상 연인 프로필 태그 생성
     * @param hashtags
     * @param member
     */
    public void createVirtual(List<Long> hashtags, Member member) {
        for (Long id : hashtags) {
            HashTag hashTag = hashTagRepository.findById(id).orElseThrow();
            virtualMateHashtagRepository.save(new VirtualMateHashtag(
                    member,
                    hashTag
            ));
        }
    }

    /**
     * 가상 연인 프로필 태그 가져오기
     * @param member
     * @return
     */
    @Transactional
    public List<HashTag> getVirtualTag(Member member) {
        List<VirtualMateHashtag> list = virtualMateHashtagRepository.findAllByMember(member);
        ArrayList<HashTag> hashTags = new ArrayList<>();
        for (VirtualMateHashtag v : list) {
            hashTags.add(v.getHashTag());
        }
        return hashTags;
    }

    @Transactional
    public void changeProfilePhoto(Member member, MultipartFile photo) {
        Photo previousPhoto = member.getProfilePhoto();

        Photo photo1 = null;
        try {
            photo1 = photoService.saveProfilePhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (previousPhoto != null) {
            photoService.deletePhoto(previousPhoto.getId());
        }
        member.changeProfilePhoto(photo1);
    }

    @Transactional
    public void changeProfile(Member member, String nickName) {
        member.changeProfile(nickName);
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


}
