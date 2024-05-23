package towssome.server.repository;

import towssome.server.dto.SubscribePageDTO;
import towssome.server.entity.Member;

public interface SubscribeRepositoryCustom {

    SubscribePageDTO subscribeSlice(Member member, int page, int offset);

}
