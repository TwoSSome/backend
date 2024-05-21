package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;
import towssome.server.entity.VirtualMateHashtag;

import java.util.List;

public interface VirtualMateHashtagRepository extends JpaRepository<VirtualMateHashtag,Long> {

    List<VirtualMateHashtag> findAllByMember(Member member);

}
