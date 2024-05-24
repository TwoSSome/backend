package towssome.server.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import towssome.server.entity.CommentLike;

import static towssome.server.entity.QCommentLike.commentLike;

@RequiredArgsConstructor
@Slf4j
public class CommentLikeRepositoryImpl implements CommentLikeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Long countByCommentId(Long commentId) {
        return queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .fetchOne();
    }

}
