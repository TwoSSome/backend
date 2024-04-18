package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote,Long> {
}
