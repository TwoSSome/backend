package towssome.server.repository.reviewpost;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import towssome.server.dto.CursorResult;
import towssome.server.entity.*;
import towssome.server.entity.Calendar;
import towssome.server.repository.SearchHistoryRepository;
import towssome.server.repository.member.MemberRepository;
import towssome.server.repository.viewlike.ViewLikeRepository;
import towssome.server.repository.viewlike.ViewLikeRepositoryCustom;
import towssome.server.service.ClusterService;
import towssome.server.service.HashtagClassificationService;

import java.util.*;
import java.util.stream.Collectors;

import static towssome.server.entity.QCalendarComment.calendarComment;
import static towssome.server.entity.QMember.*;
import static towssome.server.entity.QReviewPost.reviewPost;
import static towssome.server.entity.QSubscribe.*;
import static towssome.server.entity.QViewLike.viewLike;

@RequiredArgsConstructor
@Slf4j
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final ViewLikeRepository viewLikeRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ViewLikeRepositoryCustom viewLikeRepositoryCustom;
    private final ClusterService clusterService;
    private final HashtagClassificationService hashtagClassificationService;

    @Override
    public CursorResult<ReviewPost> findSubscribeReviewList(Member subscriber, Pageable pageable) {

        List<ReviewPost> content = queryFactory.select(reviewPost)
                .from(subscribe)
                .innerJoin(subscribe.followed, member)
                .innerJoin(reviewPost).on(reviewPost.member.eq(member))
                .where(subscribe.subscriber.eq(subscriber))
                .orderBy(reviewPost.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new CursorResult<>(
                content,
                pageable.getOffset(),
                hasNext
        );
    }

    @Override
    public Page<ReviewPost> findFirstPageByOrderByReviewIdDesc(Boolean recommend, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(reviewPost.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Boolean recommend, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(reviewPost.id.desc())
                .where(reviewPost.id.lt(cursorId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .where(reviewPost.id.lt(cursorId))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findMyPostFirstPageByMemberId(Long memberId, String sort, Pageable page) {
        List<ReviewPost> reviewPosts;
        reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .where(reviewPost.member.id.eq(memberId))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findByMemberIdLessThanOrderByIdDesc(Long memberId, Long cursorId, String sort, Pageable page) {
        List<ReviewPost> reviewPosts;
        JPAQuery<Long> count;
        reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId).and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        count = queryFactory
                .select(reviewPost.count())
                .where(reviewPost.member.id.eq(memberId).and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public List<Integer> findReviewInCalendarOfMonth(int year, int month, Calendar calendar) {
        return queryFactory
                .select(Expressions.numberTemplate(Integer.class, "DAY({0})", reviewPost.createDate))
                .from(reviewPost)
                .where(
                        Expressions.numberTemplate(Integer.class, "YEAR({0})", reviewPost.createDate).eq(year),
                        Expressions.numberTemplate(Integer.class, "MONTH({0})", reviewPost.createDate).eq(month),
                        calendarComment.calendar.eq(calendar)
                )
                .groupBy(Expressions.numberTemplate(Integer.class, "DAY({0})", reviewPost.createDate))
                .fetch();
    }

    private OrderSpecifier<Long> getOrderSpecifier(String sort) {
        return Objects.equals(sort, "asc") ? reviewPost.id.asc() : reviewPost.id.desc();
    }

    private BooleanExpression getLTGT(String sort, Long cursorId) {
        return Objects.equals(sort, "asc") ? reviewPost.id.gt(cursorId) : reviewPost.id.lt(cursorId);
    }

    @Override
    public CursorResult<ReviewPost> getRecommendedReviewsPage(Member jwtMember, Pageable pageable) {
        List<Member> clusterMembers = clusterService.getClusterMembers(jwtMember.getId());
        Map<ReviewPost, Double> recommendationScores = new HashMap<>();

        Map<ReviewPost, Double> interactionScores = new HashMap<>();
        Map<ReviewPost, Double> trendScores = new HashMap<>();
        Map<ReviewPost, Double> keywordScores = new HashMap<>();

        List<Member> allMembers = queryFactory.select(member)
                .from(member)
                .fetch();

        for (Member otherMember : allMembers) {
            double similarity = calculateInteractionSimilarity(jwtMember, otherMember, clusterMembers.contains(otherMember));

            List<ViewLike> viewLikes = queryFactory.select(viewLike)
                    .from(viewLike)
                    .where(viewLike.member.eq(otherMember))
                    .fetch();

            for (ViewLike viewLike : viewLikes) {
                ReviewPost reviewPost = viewLike.getReviewPost();

                interactionScores.putIfAbsent(reviewPost, similarity);
                trendScores.putIfAbsent(reviewPost, calculateTrendWeight(reviewPost));
                keywordScores.putIfAbsent(reviewPost, calculateKeywordBoost(jwtMember, reviewPost));
            }
        }

        for (ReviewPost reviewPost : interactionScores.keySet()) {
            double finalScore = interactionScores.get(reviewPost)
                    * trendScores.get(reviewPost)
                    * keywordScores.get(reviewPost);

            recommendationScores.merge(reviewPost, finalScore, Double::sum);
        }

        List<ReviewPost> sortedPosts = recommendationScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize() + 1, sortedPosts.size());
        List<ReviewPost> content = sortedPosts.subList(start, end);

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new CursorResult<>(content, pageable.getOffset(), hasNext);
    }

    private double calculateTrendWeight(ReviewPost reviewPost) {
        long viewCount = viewLikeRepositoryCustom.findViewAmountByReviewPost(reviewPost.getId());
        long likeCount = viewLikeRepositoryCustom.findLikeAmountByReviewPost(reviewPost.getId());

        return (viewCount * 0.1) + (likeCount * 0.2);
    }

    private double calculateInteractionSimilarity(Member member1, Member member2, boolean isClusterMember) {
        Set<ReviewPost> member1LikedPosts = viewLikeRepository.findLikedPostsByMember(member1);
        Set<ReviewPost> member2LikedPosts = viewLikeRepository.findLikedPostsByMember(member2);

        long commonLikes = member1LikedPosts.stream()
                .filter(member2LikedPosts::contains)
                .count();

        Set<ReviewPost> member1ViewedPosts = viewLikeRepository.findViewedPostsByMember(member1);
        Set<ReviewPost> member2ViewedPosts = viewLikeRepository.findViewedPostsByMember(member2);

        long commonViews = member1ViewedPosts.stream()
                .filter(member2ViewedPosts::contains)
                .count();

        double likeWeight = 0.7;
        double viewWeight = 0.3;
        double similarity = (likeWeight * commonLikes + viewWeight * commonViews) /
                (member1LikedPosts.size() + member1ViewedPosts.size() + member2LikedPosts.size() + member2ViewedPosts.size());

        return isClusterMember ? similarity * 1.5 : similarity;
    }


    private double calculateKeywordBoost(Member jwtMember, ReviewPost reviewPost) {
        List<String> searchedKeywords = getSearchedKeywords(jwtMember);

        List<Tuple> reviewTags = hashtagClassificationService.getHashtags(reviewPost.getId());

        long commonKeywords = searchedKeywords.stream()
                .filter(keyword -> reviewTags.stream()
                        .anyMatch(tag -> Objects.requireNonNull(tag.get(1, String.class)).equalsIgnoreCase(keyword)))
                .count();

        return 1 + (commonKeywords * 0.3);
    }

    private List<String> getSearchedKeywords(Member jwtMember) {
        return searchHistoryRepository.findKeywordsByMember(jwtMember);
    }

}
