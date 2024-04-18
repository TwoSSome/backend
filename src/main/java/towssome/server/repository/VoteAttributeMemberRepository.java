package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.VoteAttribute;
import towssome.server.entity.VoteAttributeMember;

import java.util.List;

public interface VoteAttributeMemberRepository extends JpaRepository<VoteAttributeMember,Long> {

    List<VoteAttributeMember> findAllByVoteAttribute(VoteAttribute voteAttribute);

}
