package towssome.server.repository;

import towssome.server.dto.CursorResult;
import towssome.server.entity.HashTag;

import java.util.List;

public interface HashTagRepositoryCustom {

    CursorResult<HashTag> searchHashtag(String name, int page, int size);

    List<HashTag> getHashtagRank();

}
