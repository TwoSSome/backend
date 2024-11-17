package towssome.server.repository.cluster;

import towssome.server.entity.Member;

import java.util.List;

public interface ClusterRepositoryCustom {
    List<Member> findClustersByMemberId(Long memberId);
}
