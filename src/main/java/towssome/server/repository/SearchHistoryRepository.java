package towssome.server.repository;

import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import towssome.server.entity.Member;
import towssome.server.entity.SearchHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByMemberAndKeyword(Member member, String keyword);

    @Modifying
    @Transactional
    @Query("SELECT s.keyword FROM SearchHistory s WHERE s.member = :member")
    List<String> findKeywordsByMember(Member member);
}
