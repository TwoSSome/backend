package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.entity.HashTag;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.enumrated.MatingStatus;
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

    public Mating createOffer(Member offerMember, Member obtainMember) {

        return matingRepository.save(new Mating(
                offerMember,
                obtainMember,
                MatingStatus.OFFER
        ));
    }

    @Transactional
    public Mating acceptOffer(Mating mating) {
        mating.acceptOffer();
        return mating;
    }

    public void rejectOffer(Mating mating) {
        matingRepository.delete(mating);
    }

    @Transactional
    public List<HashTag> findMateHashTags(Member member) {
        Mating mating = matingRepository.findByObtainMemberOrOfferMember(member, member).orElseThrow();
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
