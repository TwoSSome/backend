package towssome.server.repository.subscribe;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe,Long>, SubscribeRepositoryCustom {

    List<Subscribe> findAllBySubscriber(Member member);

    Slice<Subscribe> findAllBySubscriber(Member subscribe, Pageable pageable);

    boolean existsBySubscriber(Member member);

    boolean existsBySubscriberAndFollowed(Member subscriber, Member following);
}
