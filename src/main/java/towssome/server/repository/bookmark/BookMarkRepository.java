package towssome.server.repository.bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.BookMark;
import towssome.server.entity.Category;

import java.util.List;

public interface BookMarkRepository extends JpaRepository<BookMark,Long>, BookMarkRepositoryCustom {

    List<BookMark> findAllByCategory(Category category);

    Slice<BookMark> findAllByCategory(Category category, Pageable pageable);

}
