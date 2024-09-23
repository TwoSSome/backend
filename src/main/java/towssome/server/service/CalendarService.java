package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.*;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarComment;
import towssome.server.entity.Member;
import towssome.server.exception.NotFoundCalendarException;
import towssome.server.exception.NotFoundCommentException;
import towssome.server.repository.calendar_comment.CalendarCommentRepository;
import towssome.server.repository.CalendarRepository;
import towssome.server.repository.reviewpost.ReviewPostRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarCommentRepository calendarCommentRepository;
    private final CalendarRepository calendarRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final RequestService requestBuilder;

    public CalendarComment createComment(Member member, CreateCalendarCommentReq req) {

        //캘린더는 연인 신청을 받으면 자동으로 생성
        Calendar calendar = getCalendar(member);

        return calendarCommentRepository.save(new CalendarComment(
                member, req.body(), calendar, req.year(), req.month(), req.day()
        ));
    }

    // 캘린더 댓글 삭제
    @Transactional
    public void deleteComment(Long deleteId) {
        calendarCommentRepository.delete(calendarCommentRepository.findById(deleteId).orElseThrow(
                () -> new NotFoundCommentException("해당 코멘트를 찾지 못했습니다")
        ));
    }

    /**
     * 해당 멤버가 캘린더가 있으면 리뷰와 캘린더 코멘트를 작성한 날짜를 반환합니다
     * @param req
     * @param member
     * @return 리뷰, 캘린더 코멘트 작성 날짜
     */
    public CalendarExistInMonth calendarInMonth(CalendarInMonthReq req, Member member) {
        Calendar calendar = getCalendar(member);
        List<Integer> commentList = calendarCommentRepository.findCommentsInCalendarOfMonth(req.year(), req.month(), calendar);
        List<Integer> reviewList = reviewPostRepository.findReviewInCalendarOfMonth(req.year(), req.month(), calendar);

        return new CalendarExistInMonth(
                reviewList,
                commentList
        );
    }

    /**
     * 연,월,일을 입력받아 해당 날짜의 코멘트 리스트를 받아옵니다
     * @param req
     * @param member
     * @return 코멘트 리스트
     */
    public List<CalendarCommentRes> findCalendarCommentInDay(CalendarInDayReq req, Member member) {
        Calendar calendar = getCalendar(member);

        List<CalendarComment> comments =
                calendarCommentRepository.findCommentsByDay(
                        LocalDate.of(req.year(), req.month(), req.day()),
                        calendar);

        ArrayList<CalendarCommentRes> result = new ArrayList<>();

        for (CalendarComment comment : comments) {
            result.add(new CalendarCommentRes(
                    comment.getBody(),
                    comment.getId(),
                    comment.getCreateDate()
            ));
        }

        return result;
    }

    //캘린더 코멘트 업데이트
    @Transactional
    public void updateComment(CalendarCommentUpdateReq req) {

        CalendarComment comment = calendarCommentRepository.findById(req.id()).orElseThrow(
                () -> new NotFoundCalendarException("해당 캘린더 코멘트가 없습니다!")
        );

        comment.update(req.body());
    }

    //해당 멤버의 캘린더 가져오기
    private Calendar getCalendar(Member member) {
        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("아직 캘린더가 생성되지 않았습니다")
        );
        return calendar;
    }

}
