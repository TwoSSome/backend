package towssome.server.repository.hashtag_classification;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import towssome.server.entity.ReviewPost;

import java.util.List;

import static towssome.server.entity.QHashTag.hashTag;
import static towssome.server.entity.QHashtagClassification.hashtagClassification;
import static towssome.server.entity.QReviewPost.reviewPost;

@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationRepositoryImpl implements HashtagClassificationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findFirstReviewPageByHashtag(String keyword, String sort, Pageable pageable) {
        JPAQuery<ReviewPost> query = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (sort.equalsIgnoreCase("asc")) {
            query.orderBy(reviewPost.id.asc());
        } else {
            query.orderBy(reviewPost.id.desc());
        }

        List<ReviewPost> results = query.fetch();

        JPAQuery<Long> count = queryFactory
                .select(hashtagClassification.count())
                .from(hashtagClassification)
                .where(hashtagContains(keyword));

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findReviewPageByCursorIdAndHashTag(String keyword, Long cursorId, String sort, Pageable pageable) {
        Boolean asc = sort.equalsIgnoreCase("asc");

        JPAQuery<ReviewPost> query = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContains(keyword), nextReviewId(cursorId, asc))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (asc) {
            query.orderBy(reviewPost.id.asc());
        } else {
            query.orderBy(reviewPost.id.desc());
        }

        List<ReviewPost> results = query.fetch();

        JPAQuery<Long> count = queryFactory
                .select(hashtagClassification.count())
                .from(hashtagClassification)
                .where(hashtagContains(keyword), nextReviewId(cursorId, false));

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }


    @Override
    public List<Tuple> findHashtagsByReviewId(Long reviewId) {
        return queryFactory
                .select(hashtagClassification.hashTag.id, hashtagClassification.hashTag.name)
                .from(hashtagClassification)
                .where(hashtagClassification.reviewPost.id.eq(reviewId))
                .fetch();
    }




    private BooleanExpression hashtagContains(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return hashtagClassification.hashTag.name.containsIgnoreCase(keyword);
    }
    private BooleanExpression nextReviewId(Long cursorId, boolean asc) {
        if (cursorId == null) {
            return null;
        }
        if (asc) {
            return hashtagClassification.reviewPost.id.gt(cursorId);
        } else {
            return hashtagClassification.reviewPost.id.lt(cursorId);
        }
    }
}
