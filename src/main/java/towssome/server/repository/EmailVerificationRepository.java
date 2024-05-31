package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.EmailVerification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification,Long> {

    boolean existsByEmail(String email);

    Optional<EmailVerification> findByEmail(String email);

    List<EmailVerification> findByCreateDateBefore(LocalDateTime fiveMinutesAgo);

    void deleteByEmail(String email);

}
