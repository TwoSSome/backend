package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    boolean existsMemberByMemberUsingId(String memberUsingId);

}
