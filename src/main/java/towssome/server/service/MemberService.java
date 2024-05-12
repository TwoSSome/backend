package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.Member;
import towssome.server.entity.Photo;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.MemberRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PhotoService photoService;

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
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

        if(previousPhoto != null) {
            photoService.deletePhoto(previousPhoto.getId());
        }
        member.changeProfilePhoto(photo1);
    }

    @Transactional
    public void changeProfile(Member member, String nickName) {
        member.changeProfile(nickName);
    }

}
