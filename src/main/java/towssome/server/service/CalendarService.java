package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import towssome.server.dto.*;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarPost;
import towssome.server.entity.CalendarPostComment;
import towssome.server.entity.CalendarTag;
import towssome.server.repository.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService implements CalendarServiceInterface{

    private final CalendarPersonalScheduleRepository calendarPersonalScheduleRepository;
    private final CalendarScheduleRepository calendarScheduleRepository;
    private final CalendarTagRepository calendarTagRepository;
    private final CalendarPostCommentRepository calendarPostCommentRepository;
    private final CalendarPostRepository calendarPostRepository;

    @Override
    public CalendarTag createCalendarTag(CreateCalendarTagDTO dto) {
        return null;
    }

    @Override
    public void deleteCalendarTag(long id) {

    }

    @Override
    public void updateCalendarTag(UpdateCalendarTagDTO dto) {

    }

    @Override
    public List<CalendarTagInfo> getAllCalendarTagInfo(Calendar calendar) {
        return List.of();
    }

    @Override
    public CalendarPost createCalendarPost(CreateCalendarPostDTO dto) {
        return null;
    }

    @Override
    public void deleteCalendarPost(long id) {

    }

    @Override
    public void updateCalendarPost(UpdateCalendarPostDTO dto) {

    }

    @Override
    public CalendarPostComment createCalendarPostComment(CCPCDTO dto) {
        return null;
    }

    @Override
    public void deleteCalendarPostComment(long id) {

    }

    @Override
    public void updateCalendarPostComment(updateCPCDTO dto) {

    }

    @Override
    public List<SearchPoomPoomLogInfo> searchPoomPoomLogs(SearchPoomPoomLogDTO dto) {
        return List.of();
    }

    @Override
    public List<CalendarTagInfo> getDateTagInfo(Calendar calendar) {
        return List.of();
    }

    @Override
    public List<CalendarScheduleInfo> getCalendarInfoByMonth(CalendarInfoByMonthDTO dto) {
        return List.of();
    }

    @Override
    public CalendarPostDetailInfo getCalendarPostDetail(long id) {
        return null;
    }

    @Override
    public CalendarScheduleDetailInfo getCalendarScheduleDetail(long id) {
        return null;
    }

}
