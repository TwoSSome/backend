package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.entity.ReviewPost;
import towssome.server.enumrated.PhotoType;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo,Long> {

    List<Photo> findAllByReviewPostId(Long id);
    List<Photo> findAllByCommunityPostId(Long id);

    List<Photo> findAllByReviewPost(ReviewPost reviewPost);
    List<Photo> findAllByCommunityPost(CommunityPost communityPost);


}
