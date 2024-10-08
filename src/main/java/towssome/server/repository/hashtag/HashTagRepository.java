package towssome.server.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.HashTag;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag,Long>, HashTagRepositoryCustom {

    boolean existsByName(String name);

    HashTag findByName(String name);

    Optional<HashTag> findHashTagByName(String name);

}
