package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.BookMark;

public interface BookMarkRepository extends JpaRepository<BookMark,Long> {
}
