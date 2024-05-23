package towssome.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Member;
import towssome.server.entity.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply,Long> {

    List<Reply> findAllByCommunityPost(CommunityPost communityPost);

    List<Reply> findAllByAuthor(Member member);

    Slice<Reply> findAllByCommunityPost(CommunityPost communityPost, Pageable pageable);

}
