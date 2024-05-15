package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {

    boolean existsByName(String name);

    HashTag findByName(String name);

}
