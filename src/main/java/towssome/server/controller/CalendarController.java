package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CalendarComment;
import towssome.server.entity.DateCourse;
import towssome.server.entity.Member;
import towssome.server.service.CalendarService;
import towssome.server.service.DateCourseByDateReq;

import java.util.List;

@Tag(name = "캘린더")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final MemberAdvice memberAdvice;

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
            description = "요청한 날짜 사이의 데이트코스 리스트를 반환합니다, AT 필요")
    @GetMapping("/dateCource")
    public ListResultRes<List<DateCourseRes>> getDateCourse(DateCourseByDateReq req){

        Member author = memberAdvice.findJwtMember();

        List<DateCourseRes> result = calendarService.getDateCourseListByDate(req, author);

        return new ListResultRes<>(
                result
        );
    }

    @Operation(summary = "데이트코스 생성 API",
            description = "날짜와 사진을 받아 데이트코스를 생성합니다, AT 필요, 본문은 100자 이내")
    @PostMapping("/dateCourse/create")
    public ResponseEntity<?> createDateCourse(
            @RequestPart @Valid CreateDateCourseReq req,
            @RequestPart MultipartFile photo){

        Member author = memberAdvice.findJwtMember();

        DateCourse dateCourse = calendarService.createDateCourse(req, photo, author);

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

        calendarService.updateDateCourse(req);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
