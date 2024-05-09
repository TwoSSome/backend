package towssome.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.entity.Subscribe;

import java.util.List;

@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

}
