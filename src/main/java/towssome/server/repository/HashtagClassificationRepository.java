package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.HashtagClassification;

public interface HashtagClassificationRepository extends JpaRepository<HashtagClassification,Long>,HashtagClassificationRepositoryCustom {
}
