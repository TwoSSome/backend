package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
