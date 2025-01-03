package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import towssome.server.entity.Member;
import towssome.server.entity.SearchHistory;
import towssome.server.repository.SearchHistoryRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional
    public void updateSearchHistory(Member member, String keyword) {
        Optional<SearchHistory> existingSearchHistory = searchHistoryRepository.findByMemberAndKeyword(member, keyword);

        if (existingSearchHistory.isPresent()) {
            SearchHistory searchHistory = existingSearchHistory.get();
            searchHistory.setKeyword(keyword);
            return;
        }
        SearchHistory newSearchHistory = new SearchHistory();
        newSearchHistory.setMember(member);
        newSearchHistory.setKeyword(keyword);
        searchHistoryRepository.save(newSearchHistory);
    }
}
