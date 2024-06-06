package towssome.server.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Boolean existsByRefresh(String refresh);

    //실제로 사용할때는 서비스에서
    @Transactional
    void deleteByRefresh(String refresh);

    boolean existsByUsername(String username);

    RefreshToken findByUsername(String username);

}
