package towssome.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.dto.CursorResult;
import towssome.server.entity.HashTag;

import java.util.List;

import static towssome.server.entity.QHashTag.*;

@RequiredArgsConstructor
public class HashTagRepositoryImpl implements HashTagRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorResult<HashTag> searchHashtag(String name, int page, int size) {

        List<HashTag> result = queryFactory.selectFrom(hashTag)
                .where(hashTag.name.like("%" + name + "%"))
                .offset(page)
                .limit(size+1)
                .fetch();

        boolean hasNext = false;

        if (result.size() > size) {
            hasNext = true;
            result.remove(size);
        }

        return new CursorResult<>(
                result,
                (long) page + 1,
                hasNext
        );
    }
}
