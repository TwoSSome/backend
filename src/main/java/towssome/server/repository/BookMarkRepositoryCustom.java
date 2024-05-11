package towssome.server.repository;

import towssome.server.entity.BookMark;
import towssome.server.entity.Member;

import java.util.List;

public interface BookMarkRepositoryCustom {

    List<BookMark> findAllBookmarkByMember(Member member);

}
