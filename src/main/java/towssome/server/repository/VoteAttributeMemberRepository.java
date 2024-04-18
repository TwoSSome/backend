package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.VoteAttributeMember;

public interface VoteAttributeMemberRepository extends JpaRepository<VoteAttributeMember,Long> {
}
