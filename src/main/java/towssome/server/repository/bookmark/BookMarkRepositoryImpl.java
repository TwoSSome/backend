package towssome.server.repository.bookmark;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.entity.*;

import java.util.List;

import static towssome.server.entity.QBookMark.*;
import static towssome.server.entity.QCategory.*;
import static towssome.server.entity.QMember.*;

@RequiredArgsConstructor
public class BookMarkRepositoryImpl implements BookMarkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BookMark> findAllBookmarkByMember(Member findMember) {

        return queryFactory.select(bookMark)
                .from(bookMark)
                .innerJoin(bookMark.category, category)
                .innerJoin(category.member, member)
                .where(member.eq(findMember))
                .fetch();
    }
}
