package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.BookMark;
import towssome.server.entity.Category;

import java.util.List;

public interface BookMarkRepository extends JpaRepository<BookMark,Long>, BookMarkRepositoryCustom {

    List<BookMark> findAllByCategory(Category category);


}
