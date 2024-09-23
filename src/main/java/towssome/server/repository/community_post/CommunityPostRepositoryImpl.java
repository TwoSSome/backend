package towssome.server.repository.community_post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import towssome.server.dto.CommunitySearchCondition;
import towssome.server.entity.CommunityPost;

import java.util.List;

import static org.springframework.util.StringUtils.*;
import static towssome.server.entity.QCommunityPost.*;

@RequiredArgsConstructor
@Slf4j
public class CommunityPostRepositoryImpl implements CommunityPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommunityPost> pagingCommunityPostSearch(CommunitySearchCondition cond, Pageable pageable) {
        List<CommunityPost> result = queryFactory
                .selectFrom(communityPost)
                .where(
                        titleAndBodyContains(cond.keyword()),
                        nicknameEq(cond.nickname())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(
                        titleAndBodyContains(cond.keyword()),
                        nicknameEq(cond.nickname()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression nicknameEq(String nickname) {
        return hasText(nickname) ? communityPost.author.nickName.like("%"+nickname+"%") : null;
    }

    private BooleanExpression titleAndBodyContains(String keyword) {
        return hasText(keyword) ? communityPost.title.like("%"+keyword+"%").or(communityPost.body.like("%"+keyword+"%")) : null;
    }


}
