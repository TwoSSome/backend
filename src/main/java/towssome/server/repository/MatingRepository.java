package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;
import towssome.server.enumrated.MatingStatus;

import java.util.List;
import java.util.Optional;

public interface MatingRepository extends JpaRepository<Mating,Long> {

    Optional<Mating> findByObtainMember(Member member);

    @Query("SELECT m FROM Mating m WHERE m.obtainMember = :member OR m.offerMember = :member")
    Optional<Mating> findMatingByMember(@Param("member") Member member);

    boolean existsByOfferMember(Member member);

    boolean existsByObtainMember(Member member);

}
