package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.ImageMetaDataAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.*;
import towssome.server.enumrated.PhotoType;
import towssome.server.exception.NotFoundCalendarException;
import towssome.server.exception.NotFoundCommentException;
import towssome.server.exception.NotFoundDateCourseException;
import towssome.server.repository.date_course.DateCourseRepository;
import towssome.server.repository.calendar_comment.CalendarCommentRepository;
import towssome.server.repository.CalendarRepository;
import towssome.server.repository.reviewpost.ReviewPostRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarCommentRepository calendarCommentRepository;
    private final CalendarRepository calendarRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final DateCourseRepository dateCourseRepository;
    private final PhotoAdvice photoAdvice;
    private final ImageMetaDataAdvice imageMetaDataAdvice;

    public CalendarComment createComment(Member member, CreateCalendarCommentReq req) {

        //캘린더는 연인 신청을 받으면 자동으로 생성
        Calendar calendar = getCalendar(member);

        return calendarCommentRepository.save(new CalendarComment(
                member, req.body(), calendar, new RegistrationDate(req.year(), req.month(), req.day())
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
     * 해당 멤버가 캘린더가 있으면 리뷰와 캘린더 코멘트, 데이트코스를 작성한 날짜를 반환합니다
     * @param req
     * @param member
     * @return 리뷰, 캘린더 코멘트, 데이트코스 작성 날짜
     */
    public CalendarExistInMonth calendarInMonth(CalendarInMonthReq req, Member member) {
        Calendar calendar = getCalendar(member);
        List<Integer> commentList = calendarCommentRepository.findCommentsInCalendarOfMonth(req.year(), req.month(), calendar);
        List<Integer> reviewList = reviewPostRepository.findReviewInCalendarOfMonth(req.year(), req.month(), calendar);
        List<Integer> dateCourseList = dateCourseRepository.ExistDateCourseByDate(req, calendar);

        return new CalendarExistInMonth(
                reviewList,
                commentList,
                dateCourseList
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

    /**
     * 사진과 날짜를 입력받아 데이트코스를 저장합니다. 1 데이트코스 = 1 사진
     * @param req
     * @param file
     * @param member
     * @return
     */
    public DateCourse createDateCourse(CreateDateCourseReq req, MultipartFile file, Member member) {
        GpsInformationDTO extract = imageMetaDataAdvice.extract(file);
        Photo photo = photoAdvice.savePhoto(file, PhotoType.DATE_COURSE);
        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("아직 캘린더가 없습니다")
        );
        DateCourse dateCourse = new DateCourse(
                req.body(),
                member,
                calendar,
                new GPS(
                        extract.longitude() != null ? extract.longitude() : null,
                        extract.latitude() != null ? extract.latitude() : null
                ),
                new RegistrationDate(
                        req.year(),
                        req.month(),
                        req.day()
                ),
                photo
        );

        return dateCourseRepository.save(dateCourse);
    }

    //데이트코스 삭제
    @Transactional
    public void deleteDateCourse(Long id) {
        DateCourse dateCourse = dateCourseRepository.findById(id).orElseThrow(
                () -> new NotFoundDateCourseException("해당 데이트코스가 없습니다")
        );
        Photo photo = dateCourse.getPhoto();
        dateCourseRepository.delete(dateCourse);
        photoAdvice.deletePhoto(photo.getId());
    }

    /**
     * 시작날짜와 끝 날짜를 받아 그 사이 날짜의 데이트코스 리스트 반환
     * @param req
     * @param member
     * @return 날짜, 사진, 작성자 닉네임, 작성자 프로필사진
     */
    public List<DateCourseRes> getDateCourseListByDate(DateCourseByDateReq req, Member member) {
        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("아직 캘린더가 없습니다")
        );

        List<DateCourse> dateCourseByDate = dateCourseRepository.findDateCourseByDate(req, calendar);
        List<DateCourseRes> result = new ArrayList<>();

        for (DateCourse item : dateCourseByDate) {
            result.add(new DateCourseRes(
                    item.getId(),
                    item.getDate().getRegistrationYear(),
                    item.getDate().getRegistrationMonth(),
                    item.getDate().getRegistrationDay(),
                    item.getBody(),
                    item.getPhoto().getS3Path(),
                    item.getAuthor().getNickName(),
                    item.getAuthor().getProfilePhoto().getS3Path()
            ));
        }

        return result;
    }

    @Transactional
    public DateCourse updateDateCourse(UpdateDateCourseReq req) {
        DateCourse dateCourse = dateCourseRepository.findById(req.id()).orElseThrow(
                () -> new NotFoundDateCourseException("해당 데이트코스가 없습니다")
        );
        dateCourse.update(req.body());
        return dateCourse;
    }


    //해당 멤버의 캘린더 가져오기
    private Calendar getCalendar(Member member) {
        Calendar calendar = calendarRepository.findByAuth(member).orElseThrow(
                () -> new NotFoundCalendarException("아직 캘린더가 생성되지 않았습니다")
        );
        return calendar;
    }

}
