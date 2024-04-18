package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Vote;
import towssome.server.entity.VoteAttribute;

import java.util.List;
import java.util.Optional;

public interface VoteAttributeRepository extends JpaRepository<VoteAttribute,Long> {

    List<VoteAttribute> findAllByVote(Vote vote);

}
