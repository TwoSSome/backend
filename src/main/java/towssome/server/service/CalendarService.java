package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.*;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarPost;
import towssome.server.entity.CalendarPostComment;
import towssome.server.entity.CalendarTag;
import towssome.server.exception.NotFoundCalendarException;
import towssome.server.exception.NotFoundEntityException;
import towssome.server.repository.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService implements CalendarServiceInterface{

    private final CalendarPersonalScheduleRepository calendarPersonalScheduleRepository;
    private final CalendarScheduleRepository calendarScheduleRepository;
    private final CalendarTagRepository calendarTagRepository;
    private final CalendarPostCommentRepository calendarPostCommentRepository;
    private final CalendarPostRepository calendarPostRepository;
    private final CalendarRepository calendarRepository;

    @Override
    public CalendarTag createCalendarTag(CreateCalendarTagDTO dto) {

        Calendar calendar = calendarRepository.findByAuth(dto.member()).orElseThrow(
                () -> new NotFoundCalendarException("해당 멤버의 캘린더가 없습니다")
        );
        List<CalendarTag> allTagInCalendar = calendarTagRepository.findAllByCalendar(calendar);
        if (allTagInCalendar.size() >= 8) { // 총 태그의 사이즈는 개인 태그를 제외하고 총 8개
            throw new RuntimeException("생성 가능한 태그 개수 초과");
        }

        return calendarTagRepository.save(new CalendarTag(
                dto.name(), dto.color(), calendar, false
        ));
    }

    @Override
    @Transactional
    public void deleteCalendarTag(long id) {
        if (!calendarTagRepository.existsById(id)) {
            throw new NotFoundEntityException("해당 태그를 찾지 못했습니다");
        }
        calendarTagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateCalendarTag(UpdateCalendarTagDTO dto) {
        CalendarTag calendarTag = calendarTagRepository.findById(dto.id()).orElseThrow(
                () -> new NotFoundCalendarException("해당 태그를 찾지 못했습니다")
        );

        calendarTag.updateTag(dto.name(), dto.color());
    }

    @Override
    public CalendarTagInfo getCalendarTag(Long id) {
        CalendarTag calendarTag = calendarTagRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException("해당 태그를 찾지 못했습니다")
        );

        return new CalendarTagInfo(
                calendarTag.getId(),
                calendarTag.getName(),
                calendarTag.getColor(),
                calendarTag.getIsDefaultTag()
        );
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
