package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPost;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo,Long> {

    List<Photo> findAllByReviewPostId(Long id);
    List<Photo> findAllByCommunityPostId(Long id);

    List<Photo> findAllByReviewPost(ReviewPost reviewPost);
    List<Photo> findAllByCommunityPost(CommunityPost communityPost);

    List<Photo> findAllByCalendarPost(CalendarPost calendarPost);
}
