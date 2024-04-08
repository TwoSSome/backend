package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CategoryClassification;

public interface CategoryClassificationRepository extends JpaRepository<CategoryClassification, Long> {
}
