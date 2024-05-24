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
import static towssome.server.entity.QHashTag.hashTag;

@RequiredArgsConstructor
@Slf4j
public class HashtagClassificationRepositoryImpl implements HashtagClassificationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findFirstReviewPageByHashtag(String keyword, String sort, Pageable pageable) {
        List<ReviewPost> results;
        if (sort.equalsIgnoreCase("asc")) {
            results = queryFactory
                    .select(hashtagClassification.reviewPost)
                    .from(hashtagClassification)
                    .join(hashtagClassification.hashTag, hashTag)
                    .where(hashtagContains(keyword))
                    .orderBy(reviewPost.id.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else {
            results = queryFactory
                    .select(hashtagClassification.reviewPost)
                    .from(hashtagClassification)
                    .join(hashtagClassification.hashTag, hashTag)
                    .where(hashtagContains(keyword))
                    .orderBy(reviewPost.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

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
        List<ReviewPost> results;
        if (sort.equalsIgnoreCase("asc")) {
            results = queryFactory
                    .select(hashtagClassification.reviewPost)
                    .from(hashtagClassification)
                    .join(hashtagClassification.hashTag, hashTag)
                    .where(hashtagContains(keyword),nextReviewId(cursorId,true))
                    .orderBy(reviewPost.id.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else {
            results = queryFactory
                    .select(hashtagClassification.reviewPost)
                    .from(hashtagClassification)
                    .join(hashtagClassification.hashTag, hashTag)
                    .where(hashtagContains(keyword),nextReviewId(cursorId,false))
                    .orderBy(reviewPost.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        JPAQuery<Long> count = queryFactory
                .select(hashtagClassification.count())
                .from(hashtagClassification)
                .where(hashtagContains(keyword))
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
