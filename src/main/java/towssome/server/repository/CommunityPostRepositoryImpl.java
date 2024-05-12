package towssome.server.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import towssome.server.dto.CommunitySearchCondition;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.QCommunityPost;

import java.util.List;

import static org.springframework.util.StringUtils.*;
import static towssome.server.entity.QCommunityPost.*;

@RequiredArgsConstructor
public class CommunityPostRepositoryImpl implements CommunityPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommunityPost> pagingCommunityPostSearch(CommunitySearchCondition cond, Pageable pageable) {
        List<CommunityPost> result = queryFactory
                .selectFrom(communityPost)
                .where(
                        titleEq(cond.title()),
                        nicknameEq(cond.nickname()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(communityPost.count())
                .from(communityPost)
                .where(
                        titleEq(cond.title()),
                        nicknameEq(cond.nickname()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression nicknameEq(String nickname) {
        return hasText(nickname) ? communityPost.author.nickName.like("%"+nickname+"%") : null;
    }

    private BooleanExpression titleEq(String title) {
        return hasText(title) ? communityPost.title.like("%"+title+"%") : null;
    }



}
