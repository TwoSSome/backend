package towssome.server.repository.hashtag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.dto.CursorResult;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;

import java.util.List;

import static towssome.server.entity.QHashTag.*;
import static towssome.server.entity.QHashtagClassification.hashtagClassification;
import static towssome.server.entity.QReviewPost.reviewPost;
import static towssome.server.entity.QViewLike.viewLike;

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

    @Override
    public List<HashTag> getHashtagRank() {
        return queryFactory
                .selectFrom(hashTag)
                .orderBy(hashTag.count.desc())
                .limit(15)
                .fetch();
    }

    @Override
    public List<HashTag> findAllReviewHashTags(){
        return queryFactory
                .selectFrom(hashTag)
                .join(hashtagClassification).on(hashtagClassification.hashTag.eq(hashTag))
                .distinct()
                .fetch();
    }

    @Override
    public List<HashTag> findMemberViewedHashTags(Member member){
        return queryFactory
                .selectFrom(hashTag)
                .join(hashtagClassification).on(hashtagClassification.hashTag.eq(hashTag))
                .join(hashtagClassification).on(hashtagClassification.reviewPost.eq(reviewPost))
                .join(reviewPost).on(viewLike.reviewPost.eq(reviewPost))
                .where(viewLike.member.id.eq(member.getId()))
                .distinct()
                .fetch();
    }
}
