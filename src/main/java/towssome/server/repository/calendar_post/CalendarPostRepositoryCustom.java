package towssome.server.repository.calendar_post;

import org.springframework.data.domain.Pageable;
import towssome.server.dto.CursorResult;
import towssome.server.dto.PoomPoomLogInfo;
import towssome.server.entity.Member;

import java.util.List;

public interface CalendarPostRepositoryCustom {
    CursorResult<PoomPoomLogInfo> findPoomPoomLogs(Member jwtMember, int page, int size);
    List<PoomPoomLogInfo> findPoomPoomLogsByMonth(Member jwtMember, int month);
}
