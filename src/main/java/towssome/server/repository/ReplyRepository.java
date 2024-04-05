package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
}
