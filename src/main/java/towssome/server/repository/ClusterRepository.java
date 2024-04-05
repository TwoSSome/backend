package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Cluster;

public interface ClusterRepository extends JpaRepository<Cluster,Long> {
}
