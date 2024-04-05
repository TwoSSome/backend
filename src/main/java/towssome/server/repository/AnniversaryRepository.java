package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Anniversary;

public interface AnniversaryRepository extends JpaRepository<Anniversary,Long> {
}
