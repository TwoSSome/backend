package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Calendar;
import towssome.server.entity.Member;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar,String> {

    @Query("SELECT c FROM Calendar c WHERE c.authMember1 = :member OR c.authMember2 = :member")
    Optional<Calendar> findByAuth(@Param("member") Member member);


}
