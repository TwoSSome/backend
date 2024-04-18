package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.VoteAttribute;

public interface VoteAttributeRepository extends JpaRepository<VoteAttribute,Long> {
}
