package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.entity.HashTag;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.enumrated.MatingStatus;
import towssome.server.exception.AlreadyExistMatingException;
import towssome.server.exception.NotFoundMatingException;
import towssome.server.repository.MatingRepository;
import towssome.server.repository.ProfileTagRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatingService {

    private final MatingRepository matingRepository;
    private final ProfileTagRepository profileTagRepository;

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
     * 요청을 수락, mating의 상태를 OFFER 로 변경
     * @param mating
     * @return
     */
    @Transactional
    public Mating acceptOffer(Mating mating) {
        if(mating.getStatus() != MatingStatus.OFFER) return null;
        mating.acceptOffer();
        return mating;
    }

    public void rejectOffer(Mating mating) {
        matingRepository.delete(mating);
    }

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
                findByObtainMemberOrOfferMemberAndStatus(member, member, MatingStatus.MATING)
                .orElseThrow(() -> new NotFoundMatingException("메이트가 없습니다"));
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
