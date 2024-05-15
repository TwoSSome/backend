package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.HashTag;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    @Modifying
    @Query("DELETE FROM HashTag h WHERE h.reviewPost.id = :reviewId AND h.id = :tagId")
    void deleteHashtag(@Param("reviewId") Long reviewId, @Param("tagId") Long tagId);

    @Query("SELECT h.name FROM HashTag h WHERE h.reviewPost.id = :reviewId")
    List<String> findHashtags(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("DELETE FROM HashTag h WHERE h.reviewPost.id = :reviewId")
    void deleteAllHashtags(@Param("reviewId") Long reviewId);
}
