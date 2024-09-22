package towssome.server.repository.community_post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.dto.CommunitySearchCondition;
import towssome.server.entity.CommunityPost;

public interface CommunityPostRepositoryCustom {

    Page<CommunityPost> pagingCommunityPostSearch(CommunitySearchCondition cond, Pageable pageable);

}
