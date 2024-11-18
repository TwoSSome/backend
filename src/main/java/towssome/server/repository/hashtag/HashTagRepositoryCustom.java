package towssome.server.repository.hashtag;

import towssome.server.dto.CursorResult;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;

import java.util.List;

public interface HashTagRepositoryCustom {

    CursorResult<HashTag> searchHashtag(String name, int page, int size);

    List<HashTag> getHashtagRank();

    List<HashTag> findAllReviewHashTags();

    List<HashTag> findMemberViewedHashTags(Member member);
}
