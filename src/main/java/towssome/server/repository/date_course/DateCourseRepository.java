package towssome.server.repository.date_course;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.DateCourse;

public interface DateCourseRepository extends JpaRepository<DateCourse,Long>, DateCourseRepositoryCustom {
}
