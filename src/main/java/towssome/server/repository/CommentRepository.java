package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Modifying
    @Query("update Comment r set r.body = :body where r.id = :id")
    void updateComment(@Param("id") Long id, @Param("body") String body);


    Page<Comment> findAllByReviewPostId(Long reviewId, Pageable pageable);
}
