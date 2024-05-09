package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe,Long>, SubscribeRepositoryCustom {

    List<Subscribe> findAllBySubscriber(Member member);



}
