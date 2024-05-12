package towssome.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.dto.SubscribeSliceDTO;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;

import java.util.List;

import static towssome.server.entity.QSubscribe.*;

@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public SubscribeSliceDTO subscribeSlice(Member member, int offset, int limit) {

        List<Subscribe> content = queryFactory
                .selectFrom(subscribe)
                .where(subscribe.subscriber.eq(member))
                .offset(offset)
                .limit(limit+1)
                .fetch();

        boolean hasNext = false;

        if (content.size() > limit) {
            hasNext = true;
            content.remove(limit);
        }

        return new SubscribeSliceDTO(
                content,
                hasNext
        );
    }
}
