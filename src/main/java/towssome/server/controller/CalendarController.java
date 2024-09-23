package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.service.CalendarService;

import java.util.List;

@Tag(name = "캘린더")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final MemberAdvice memberAdvice;

    @Operation(summary = "캘린더 월별 정보 출력 API", description = "jwt 필요")
    @GetMapping("/month")
    public CalendarExistInMonth getMonthInfo(
            @RequestParam int year,
            @RequestParam int month){

        Member member = memberAdvice.findJwtMember();

        return calendarService.calendarInMonth(new CalendarInMonthReq(year, month),member);
    }

    @Operation(summary = "캘린더 일별 코멘트 API", description = "해당 일자의 코멘트 리스트를 반환합니다, jwt 필요")
    @GetMapping("/comment")
    public List<CalendarCommentRes> getComments(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day){

        Member member = memberAdvice.findJwtMember();

        return calendarService.findCalendarCommentInDay(new CalendarInDayReq(year,month,day),member);
    }

    @Operation(summary = "캘린더 코멘트 생성 API", description = "캘린더의 코멘트를 생성합니다, jwt 필요")
    @PostMapping("/create")
    public ResponseEntity<?> createComment(CreateCalendarCommentReq req){

        Member member = memberAdvice.findJwtMember();
        calendarService.createComment(member, req);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 삭제 API", description = "해당 id의 코멘트를 삭제합니다")
    @PostMapping("/comment/delete")
    public ResponseEntity<?> deleteComment(IdReq req){

        calendarService.deleteComment(req.categoryId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 코멘트 업데이트 API", description = "해당 id의 코멘트를 업데이트합니다")
    @PostMapping("/comment/update")
    public ResponseEntity<?> updateComment(CalendarCommentUpdateReq req){

        calendarService.updateComment(req);

        return null;
    }

}
