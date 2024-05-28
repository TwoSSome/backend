package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.HashTag;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag,Long>, HashTagRepositoryCustom {

    boolean existsByName(String name);

    HashTag findByName(String name);


}
