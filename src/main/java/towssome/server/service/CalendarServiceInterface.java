package towssome.server.service;

import org.springframework.data.domain.Pageable;
import towssome.server.dto.*;
import towssome.server.entity.*;

import java.util.List;

public interface CalendarServiceInterface {

    CalendarTag createCalendarTag(CreateCalendarTagDTO dto);

    void deleteCalendarTag(long id);

    void updateCalendarTag(UpdateCalendarTagDTO dto);

    CalendarTagInfo getCalendarTag(Long id);

    CalendarSchedule createCalendarSchedule(CreateCalendarScheduleDTO dto);

    CalendarSchedule updateCalendarSchedule(UpdateCalendarScheduleDTO dto);

    void deleteCalendarSchedule(Long id);

    //작성해야할 API 2번
    List<CalendarTagInfo> getAllCalendarTagInfo(Member member);

    CalendarPost createCalendarPost(CreateCalendarPostDTO dto);

    void deleteCalendarPost(long id);

    CalendarPost updateCalendarPost(UpdateCalendarPostDTO dto);

    CalendarPostComment createCalendarPostComment(CCPCDTO dto);

    void deleteCalendarPostComment(long id);

    CalendarPostComment updateCalendarPostComment(UpdateCPCDTO dto);

    CursorResult<CPCRes> getCalendarPostComments(Long postId, Long cursorId, String sort, Pageable page);

    // 작성해야할 API 3-a
    List<SearchPoomPoomLogInfo> searchPoomPoomLogs(SearchPoomPoomLogDTO dto);

    //작성해야할 API 3-b
    List<CalendarTagInfo> getDateTagInfo(Calendar calendar);

    //작성해야할 API 4
    List<CalendarScheduleInfo> getCalendarInfoByMonth(CalendarInfoByMonthDTO dto);

    //작성해야할 API 5
    CalendarPostDetailInfo getCalendarPostDetail(long id);

    //작성해야할 API 6
    CalendarScheduleDetailInfo getCalendarScheduleDetail(long id);

}
