package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote,Long> {

    Optional<Vote> findByCommunityPost(CommunityPost communityPost);

}
