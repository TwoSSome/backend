package towssome.server.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import towssome.server.entity.ReviewPost;

import java.util.List;

import static towssome.server.entity.QHashtagClassification.hashtagClassification;
import static towssome.server.entity.QReviewPost.reviewPost;

@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationRepositoryImpl implements HashtagClassificationRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findReviewsByHashtagOrderBySort(String keyword, String sort, Pageable pageable){
        List<ReviewPost> result = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .where(hashtagContains(keyword))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(hashtagClassification.count())
                .from(hashtagClassification)
                .where(hashtagContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression hashtagContains(String keyword){
        return hashtagClassification.hashTag.name.eq(keyword);
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("asc".equalsIgnoreCase(sort)) {
            return reviewPost.createDate.asc();
        } else {
            return reviewPost.createDate.desc();
        }
    }
}