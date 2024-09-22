package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import towssome.server.entity.*;
import towssome.server.enumrated.MatingStatus;
import towssome.server.exception.AlreadyExistMatingException;
import towssome.server.exception.NotFoundMatingException;
import towssome.server.repository.CalendarRepository;
import towssome.server.repository.MatingRepository;
import towssome.server.repository.ProfileTagRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatingService {

    private final MatingRepository matingRepository;
    private final ProfileTagRepository profileTagRepository;
    private final CalendarRepository calendarRepository;

    public Mating findById(Long id) {
        return matingRepository.findById(id).orElseThrow();
    }

    /**
     * 신청하는 멤버와 신청받을 멤버를 선택해서 메이팅 신청 create,
     * 이미 신청하는 쪽에서 offer가 만들어져 있으면 exception 발생
     * @param offerMember
     * @param obtainMember
     * @return
     */
    public Mating createOffer(Member offerMember, Member obtainMember) {

        if(matingRepository.existsByOfferMember(offerMember))
            throw new AlreadyExistMatingException("이미 메이트가 존재합니다");

        return matingRepository.save(new Mating(
                offerMember,
                obtainMember,
                MatingStatus.OFFER
        ));
    }

    /**
     * 요청을 수락, mating의 상태를 MATING 으로 변경, 둘 사이의 calendar 생성
     * @param mating
     * @return
     */
    @Transactional
    public Mating acceptOffer(Mating mating) {
        if(mating.getStatus() != MatingStatus.OFFER) return null;
        mating.acceptOffer();
        // Calendar 엔티티 id = Mating 엔티티의 두 멤버의 username
        String calendarId = mating.getOfferMember().getUsername() + "-" + mating.getObtainMember().getUsername();
        calendarRepository.save(new Calendar(
                calendarId,
                mating.getOfferMember(),
                mating.getObtainMember()
        ));
        return mating;
    }

    /**
     * 요청 거절, mating 삭제
     * @param mating
     */
    @Transactional
    public void rejectOffer(Mating mating) {
        matingRepository.delete(mating);
    }

    /**
     * 내가 한 연인 신청이나 내가 받은 연인 요청을 받아옵니다
     * @param member
     * @return
     */
    public Mating findMyMating(Member member) {
        return matingRepository.findByObtainMemberOrOfferMember(member, member)
                .orElseThrow(() -> new NotFoundMatingException("메이팅 신청이나 요청이 없습니다"));
    }

    /**
     * 메이팅 신청이 수락된 상대의 프로필 태그 리스트를 반환
     * @param member
     * @return
     */
    @Transactional
    public List<HashTag> findMateHashTags(Member member) {
        Mating mating = matingRepository.
                findByObtainMemberOrOfferMember(member, member)
                .orElseThrow(() -> new NotFoundMatingException("메이트가 없습니다"));
        if(mating.getStatus() == MatingStatus.OFFER) return null;
        ArrayList<HashTag> result = new ArrayList<>();

        if (mating.getObtainMember().equals(member)) {
            List<ProfileTag> list = profileTagRepository.findAllByMember(mating.getOfferMember());
            for (ProfileTag profileTag : list) {
                result.add(profileTag.getHashTag());
            }
        } else {
            List<ProfileTag> list = profileTagRepository.findAllByMember(mating.getObtainMember());
            for (ProfileTag profileTag : list) {
                result.add(profileTag.getHashTag());
            }
        }

        return result;
    }

}
