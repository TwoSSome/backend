package towssome.server.repository;

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
import towssome.server.dto.QuickRecommendReq;
import towssome.server.entity.QHashtagClassification;
import towssome.server.entity.ReviewPost;

import java.util.List;

import static towssome.server.entity.QHashtagClassification.hashtagClassification;
import static towssome.server.entity.QReviewPost.reviewPost;
import static towssome.server.entity.QHashTag.hashTag;

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
                .where(hashtagContains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findReviewPageByCursorIdAndHashTag(String keyword, Long cursorId, String sort, Pageable pageable) {
        JPAQuery<ReviewPost> query = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContains(keyword),nextReviewId(cursorId,true))
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
                .where(hashtagContains(keyword),nextReviewId(cursorId,false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    @Override
    public List<String> findHashtagsByReviewId(Long reviewId) {
        return queryFactory
                .select(hashtagClassification.hashTag.name)
                .from(hashtagClassification)
                .where(hashtagClassification.reviewPost.id.eq(reviewId))
                .fetch();
    }

    @Override
    public Page<ReviewPost> findFirstRecommendPageByHashtag(QuickRecommendReq req, String sort, Pageable pageable){
        JPAQuery<ReviewPost> query = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContainsAgeOrInterest(req))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (sort.equalsIgnoreCase("asc")) {
            query.orderBy(reviewPost.id.asc());
        } else {
            query.orderBy(reviewPost.id.desc());
        }

        List<ReviewPost> results = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hashtagClassification.reviewPost.count())
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContainsAgeOrInterest(req));

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);

    }

    @Override
    public Page<ReviewPost> findRecommendPageByCursorIdAndHashTag(QuickRecommendReq req, Long cursorId, String sort, Pageable pageable){
        JPAQuery<ReviewPost> query = queryFactory
                .select(hashtagClassification.reviewPost)
                .from(hashtagClassification)
                .join(hashtagClassification.hashTag, hashTag)
                .where(hashtagContainsAgeOrInterest(req), nextReviewId(cursorId, true))
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
                .where(hashtagContainsAgeOrInterest(req), nextReviewId(cursorId, false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }


    private BooleanExpression hashtagContainsAgeOrInterest(QuickRecommendReq req) {
        if (req == null) {
            return null;
        }

        BooleanExpression ageCondition = ageRangeCondition(req.ageTag());
        BooleanExpression interestCondition = hashTag.name.containsIgnoreCase(req.interestTag());

        QHashtagClassification subHashtagClassification = new QHashtagClassification("subHashtagClassification");

        JPQLQuery<Long> subQuery = JPAExpressions
                .select(subHashtagClassification.reviewPost.id)
                .from(subHashtagClassification)
                .join(subHashtagClassification.hashTag, hashTag)
                .where(ageCondition.or(interestCondition))
                .groupBy(subHashtagClassification.reviewPost.id)
                .having(subHashtagClassification.count().goe(1L)); // 수정된 부분

        return hashtagClassification.reviewPost.id.in(subQuery);
    }



    private BooleanExpression hashtagContains(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return hashtagClassification.hashTag.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression ageRangeCondition(String ageTag) {
        if (ageTag == null || ageTag.isEmpty()) {
            return null;
        }

        int midAge;
        if (ageTag.contains("10대")) {
            midAge = 15;
        } else if (ageTag.contains("20대")) {
            midAge = 25;
        } else if (ageTag.contains("30대")) {
            midAge = 35;
        } else if (ageTag.contains("40대")) {
            midAge = 45;
        } else if (ageTag.contains("50대")) {
            midAge = 55;
        } else if (ageTag.contains("60대")) {
            midAge = 65;
        } else {
            return hashTag.name.containsIgnoreCase(ageTag);
        }

        if (ageTag.contains("초반")) {
            return hashTag.name.in(getAgeRange(midAge - 3));
        } else if (ageTag.contains("중반")) {
            return hashTag.name.in(getAgeRange(midAge));
        } else if (ageTag.contains("후반")) {
            return hashTag.name.in(getAgeRange(midAge + 3));
        } else {
            return hashTag.name.containsIgnoreCase(ageTag);
        }
    }

    private List<String> getAgeRange(int midAge) {
        return List.of(
                (midAge - 5) + "살",
                (midAge - 4) + "살",
                (midAge - 3) + "살",
                (midAge - 2) + "살",
                (midAge - 1) + "살",
                midAge + "살",
                (midAge + 1) + "살",
                (midAge + 2) + "살",
                (midAge + 3) + "살",
                (midAge + 4) + "살",
                (midAge + 5) + "살",
                (midAge/10)*10 +"대"
        );
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
