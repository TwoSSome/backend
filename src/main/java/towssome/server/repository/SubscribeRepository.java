package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe,Long> {
}
