package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.ReviewPost;

public interface ReviewPostRepository extends JpaRepository<ReviewPost,Long> {

}
