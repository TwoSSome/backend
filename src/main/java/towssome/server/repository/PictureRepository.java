package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Picture;

public interface PictureRepository extends JpaRepository<Picture,Long> {
}
