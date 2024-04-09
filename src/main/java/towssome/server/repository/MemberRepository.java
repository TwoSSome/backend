package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long> {

    boolean existsMemberByMemberUsingId(String memberUsingId);

}
