package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MatingRepository extends JpaRepository<Mating,Long> {

    Optional<Mating> findByObtainMember(Member member);

    Optional<Mating> findByObtainMemberOrOfferMember(Member member1, Member member2);

}
