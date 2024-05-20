package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;

import java.util.List;

public interface ProfileTagRepository extends JpaRepository<ProfileTag,Long> {

    List<ProfileTag> findAllByMember(Member member);

}
