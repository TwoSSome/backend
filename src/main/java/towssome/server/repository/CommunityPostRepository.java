package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommunityPost;

public interface CommunityPostRepository extends JpaRepository<CommunityPost,Long> {
}
