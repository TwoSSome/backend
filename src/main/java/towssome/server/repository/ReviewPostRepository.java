package towssome.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.ReviewPost;

import java.util.List;


public interface ReviewPostRepository extends JpaRepository<ReviewPost,Long> {
    List<ReviewPost> findAllByOrderByIdDesc(Pageable page);

    List<ReviewPost> findByIdLessThanOrderByIdDesc(Long id, Pageable page);

    Boolean existsByIdLessThan(Long id);
}
