package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;

public interface UserRepository extends JpaRepository<Member,Long> {
}
