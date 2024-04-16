package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.entity.ReviewPost;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo,Long> {

    List<Photo> findAllByReviewPostId(Long id);
    List<Photo> findAllByCommunityPostId(Long id);

    List<Photo> findAllByReviewPost(ReviewPost reviewPost);
    List<Photo> findAllByCommunityPost(CommunityPost communityPost);

    @Query("SELECT p.s3Path FROM Photo p WHERE p.reviewPost.id = :reviewId")
    List<String> findS3PathByReviewPostId(Long reviewId);
}
