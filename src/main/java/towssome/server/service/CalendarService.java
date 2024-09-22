package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarComment;
import towssome.server.entity.Member;
import towssome.server.exception.NotFoundCalendarException;
import towssome.server.exception.NotFoundCommentException;
import towssome.server.repository.calendar_comment.CalendarCommentRepository;
import towssome.server.repository.CalendarRepository;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarCommentRepository calendarCommentRepository;
    private final CalendarRepository calendarRepository;

    public CalendarComment createComment(Member member, String body) {

        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("아직 캘린더가 생성되지 않았습니다")
        );

        return calendarCommentRepository.save(new CalendarComment(
                member, body, calendar
        ));
    }

    @Transactional
    public void deleteComment(Long deleteId) {
        calendarCommentRepository.delete(calendarCommentRepository.findById(deleteId).orElseThrow(
                () -> new NotFoundCommentException("해당 코멘트를 찾지 못했습니다")
        ));
    }





}
