package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
