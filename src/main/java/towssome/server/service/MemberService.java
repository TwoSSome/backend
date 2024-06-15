package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.ProfileRes;
import towssome.server.dto.RankerRes;
import towssome.server.entity.*;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Transactional
    public void createVirtual(List<String> hashtags, Member member, MultipartFile file, String mateName) {

        Photo photo = photoService.saveVirtualPhoto(file);
        member.changeVirtualPhoto(photo);
        member.changeVirtualMateName(mateName);

        for (String hashtag : hashtags) {
            if (hashTagRepository.existsByName(hashtag)) {
                HashTag hashTag = hashTagRepository.findHashTagByName(hashtag).orElseThrow();
                virtualMateHashtagRepository.save(new VirtualMateHashtag(
                        member,
                        hashTag
                ));
            }else{
                HashTag save = hashTagRepository.save(new HashTag(
                        hashtag,
                        0L
                ));
                virtualMateHashtagRepository.save(new VirtualMateHashtag(
                        member,
                        save
                ));
            }
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
    public void changeProfile(String username, String nickName, List<String> tagList) {

        Member member = memberRepository.findByUsername(username).orElseThrow();

        member.changeProfile(nickName);
        profileTagRepository.deleteAllByMember(member);

        ArrayList<HashTag> hashTags = new ArrayList<>();

        for (String name : tagList) {
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

    @Transactional
    public List<String> getProfileTagString(Member member) {
        ArrayList<String > result = new ArrayList<>();

        List<ProfileTag> list = profileTagRepository.findAllByMember(member);
        for (ProfileTag profileTag : list) {
            result.add(profileTag.getHashTag().getName());
        }

        return result;
    }

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


}
