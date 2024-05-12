package towssome.server.repository;

import towssome.server.dto.SubscribeSliceDTO;
import towssome.server.entity.Member;

public interface SubscribeRepositoryCustom {

    SubscribeSliceDTO subscribeSlice(Member member, int page, int offset);

}
