package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Cluster;

import java.util.List;

public interface ClusterRepository extends JpaRepository<Cluster,Long> {
    void deleteAllBy();
    Cluster findByMemberId(long memberId);
    List<Cluster> findByClusterNum(Long clusterNum);
}
