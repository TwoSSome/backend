package towssome.server.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import towssome.server.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByEmail(String email);
}
