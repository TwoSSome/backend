package towssome.server.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long>,CommentRepositoryCustom {
    @Modifying @Transactional
    @Query("update Comment r set r.body = :body where r.id = :id")
    void updateComment(@Param("id") Long id, @Param("body") String body);

    @Modifying @Transactional
    @Query("update Comment r set r.fixFlag = :fixFlag where r.id = :id")
    void updateFixFlag(@Param("id") Long id, @Param("fixFlag") Boolean fixFlag);
}
