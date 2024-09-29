package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.ImageMetaDataAdvice;
import towssome.server.advice.MemberAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CalendarComment;
import towssome.server.entity.DateCourse;
import towssome.server.entity.Member;
import towssome.server.entity.Photo;
import towssome.server.enumrated.PhotoType;
import towssome.server.exception.BodyOverException;
import towssome.server.exception.IllegalDateException;
import towssome.server.service.CalendarService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "캘린더")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final MemberAdvice memberAdvice;
    private final ImageMetaDataAdvice imageMetaDataAdvice;
    private final PhotoAdvice photoAdvice;

    @Operation(summary = "캘린더 월별 정보 출력 API", description = "AT 필요")
    @GetMapping("/month")
    public CalendarExistInMonth getMonthInfo(
            @RequestParam int year,
            @RequestParam int month){

        Member member = memberAdvice.findJwtMember();

        return calendarService.calendarInMonth(new CalendarInMonthReq(year, month),member);
    }

    @Operation(summary = "캘린더 일별 코멘트 API", description = "해당 일자의 코멘트 리스트를 반환합니다, AT 필요")
    @GetMapping("/comment")
    public ListResultRes<List<CalendarCommentRes>> getComments(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day){

        Member member = memberAdvice.findJwtMember();

        return new ListResultRes<>(
                calendarService.findCalendarCommentInDay(new CalendarInDayReq(year,month,day),member)
        );
    }

    @Operation(summary = "캘린더 코멘트 생성 API", description = "캘린더의 코멘트를 생성합니다, AT 필요")
    @PostMapping("/comment/create")
    public ResponseEntity<?> createComment(CreateCalendarCommentReq req){

        Member member = memberAdvice.findJwtMember();
        CalendarComment comment = calendarService.createComment(member, req);

        return new ResponseEntity<>(new CreateRes(
                comment.getId()
        ),HttpStatus.OK);
    }

    @Operation(summary = "캘린더 코멘트 삭제 API", description = "해당 id의 코멘트를 삭제합니다")
    @PostMapping("/comment/delete")
    public ResponseEntity<?> deleteComment(IdReq req){

        calendarService.deleteComment(req.id());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 코멘트 업데이트 API", description = "해당 id의 코멘트를 업데이트합니다")
    @PostMapping("/comment/update")
    public ResponseEntity<?> updateComment(CalendarCommentUpdateReq req){

        calendarService.updateComment(req);

        return null;
    }

    @Operation(summary = "데이트코스 가져오기 API",
            description = "요청한 날짜 사이의 데이트코스 리스트를 반환합니다, AT 필요",
    parameters = {@Parameter(name = "start", description = "검색을 시작할 날짜, yyyy-MM-DD 형식으로 전송"),
    @Parameter(name = "end", description = "검색 끝 날짜, yyyy-MM-DD 형식으로 전송")})
    @GetMapping("/dateCource")
    public ListResultRes<List<DateCourseRes>> getDateCourse(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        if (start.isAfter(end)) {
            throw new IllegalDateException("시작 날짜는 항상 끝 날짜 이전이어야 합니다");
        }

        Member author = memberAdvice.findJwtMember();

        List<DateCourseRes> result = calendarService.getDateCourseListByDate(start, end, author);

        return new ListResultRes<>(
                result
        );
    }

    @Operation(summary = "데이트코스 생성 API",
            description = "날짜와 사진을 받아 데이트코스를 생성합니다, AT 필요, 본문은 100자 이내, 사진은 필수입니다")
    @PostMapping("/dateCourse/create")
    public ResponseEntity<?> createDateCourse(
            @RequestPart CreateDateCourseReq req,
            @RequestPart MultipartFile photo){

        if (req.body().length() > 100) {
            throw new BodyOverException("본문은 100자 이내여야 합니다");
        }

        Member author = memberAdvice.findJwtMember();

        Photo savePhoto = photoAdvice.savePhoto(photo, PhotoType.DATE_COURSE);
        GpsInformationDTO extract = imageMetaDataAdvice.extract(photo);
        DateCourse dateCourse = calendarService.createDateCourse(req, savePhoto, author, extract);

        return new ResponseEntity<>(new CreateRes(
                dateCourse.getId()
        ), HttpStatus.OK);
    }

    @Operation(summary = "데이트코스 삭제 API",
    description = "id를 입력받아 데이트코스를 삭제합니다")
    @PostMapping("/dateCourse/delete")
    public ResponseEntity<?> deleteDateCourse(@RequestBody IdReq req){

        calendarService.deleteDateCourse(req.id());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "데이트코스 업데이트 API",
            description = "수정할 본문을 100자 이내로 작성하여 업데이트합니다")
    @PostMapping("/dateCourse/update")
    public ResponseEntity<?> updateDateCourse(@RequestBody @Valid UpdateDateCourseReq req){

        if (req.body().length() > 100) {
            throw new BodyOverException("본문은 100자 이내여야 합니다");
        }

        calendarService.updateDateCourse(req);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
