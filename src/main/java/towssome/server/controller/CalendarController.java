package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.*;
import towssome.server.exception.InvalidMonthException;
import towssome.server.exception.NotFoundEntityException;
import towssome.server.exception.PageException;
import towssome.server.exception.UnauthorizedActionException;
import towssome.server.service.CalendarServiceInterface;

import java.io.IOException;
import java.util.List;

@Tag(name = "캘린더", description = "모든 응답에는 AT가 필요합니다!!")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarServiceInterface calendarService;
    private final MemberAdvice memberAdvice;
    private final PhotoAdvice photoAdvice;

    @Operation(summary = "캘린더 일정 조회 API",
            description = "해당 월의 일정을 조회합니다. 캘린더를 찾지 못하면 404 에러를 반환합니다, AT가 필요합니다",
            parameters = {@Parameter(name = "year", description = "조회할 연도"),
                    @Parameter(name = "month", description = "조회할 달")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캘린더 조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarScheduleInfo.class))),
            @ApiResponse(responseCode = "404", description = "캘린더가 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @GetMapping("")
    public ResponseEntity<?> getCalendarInfo(
            @RequestParam int year,
            @RequestParam int month
    ) {
        Member jwtMember = memberAdvice.findJwtMember();
        List<CalendarScheduleInfo> monthInfoList = calendarService.getCalendarInfoByMonth(new CalendarInfoByMonthDTO(
                jwtMember,
                year,
                month
        ));
        return new ResponseEntity<>(
                new ListResultRes<>(monthInfoList), HttpStatus.OK
        );
    }

    @Operation(summary = "모든 태그 조회 API",
            description = "모든 태그를 조회합니다. 해당 태그를 찾지 못하면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarTagInfo.class))),
            @ApiResponse(responseCode = "404", description = "아직 캘린더가 생성되지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @GetMapping("/tag")
    public ResponseEntity<?> getAllTag(){

        Member jwtMember = memberAdvice.findJwtMember();
        List<CalendarTagInfo> allCalendarTagInfo = calendarService.getAllCalendarTagInfo(jwtMember);

        return new ResponseEntity<>(
                new ListResultRes<>(allCalendarTagInfo), HttpStatus.OK
        );
    }

    @Operation(summary = "태그 조회 API",
            description = "태그를 조회합니다. 해당 태그를 찾지 못하면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarTagInfo.class))),
            @ApiResponse(responseCode = "404", description = "태그 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @GetMapping("/tag/{id}")
    public ResponseEntity<?> getTag(@PathVariable Long id) {

        CalendarTagInfo calendarTag = calendarService.getCalendarTag(id);

        return new ResponseEntity<>(calendarTag, HttpStatus.OK);
    }

    @Operation(summary = "커스텀 태그 생성 API",
            description = "커스텀 태그를 생성합니다. 생성 개수 제한에 걸리면 409 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateRes.class))),
            @ApiResponse(responseCode = "409", description = "개수 제한"
            )
    })
    @PostMapping("/tag/create")
    public ResponseEntity<?> createCalendarTag(CreateCalendarTagReq req) {

        Member jwtMember = memberAdvice.findJwtMember();
        CalendarTag calendarTag = null;
        try {
            calendarTag = calendarService.createCalendarTag(new CreateCalendarTagDTO(
                    jwtMember,
                    req.name(),
                    req.color()
            ));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); //태그 생성 개수 제한에 걸렸음
        }

        return new ResponseEntity<>(
                new CreateRes(calendarTag.getId()),
                HttpStatus.OK);
    }

    @Operation(summary = "커스텀 태그 업데이트 API",
            description = "커스텀 태그를 수정합니다. 해당 태그를 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 수정 성공"
            ),
            @ApiResponse(responseCode = "404", description = "태그 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/tag/{id}/update")
    public ResponseEntity<?> updateCalendarTag(@PathVariable Long id, UpdateCalendarTagReq req) {

        calendarService.updateCalendarTag(new UpdateCalendarTagDTO(
                id,
                req.name(),
                req.color()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "커스텀 태그 삭제 API",
            description = "커스텀 태그를 삭제합니다. 해당 태그를 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "태그 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/tag/{id}/delete")
    public ResponseEntity<?> deleteCalendarTag(@PathVariable Long id) {

        calendarService.deleteCalendarTag(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 조회 API",
            description = "캘린더 게시글을 조회합니다. 해당 게시글을 찾지 못하면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarPostDetailInfo.class))),
            @ApiResponse(responseCode = "404", description = "게시글 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getCalendarPost(@PathVariable Long id) {

        CalendarPostDetailInfo calendarPostDetail = calendarService.getCalendarPostDetail(id);

        return new ResponseEntity<>(calendarPostDetail, HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 생성 API",
            description = "캘린더 게시글을 생성합니다. 캘린더 일정의 ID가 올바르지 않으면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateRes.class))),
            @ApiResponse(responseCode = "404", description = "게시글 일정 ID가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/post/create")
    public ResponseEntity<?> createCalendarPost(
            @RequestPart CreateCalendarPostReq req,
            @RequestPart(required = false) List<MultipartFile> photos) throws IOException {

        Member jwtMember = memberAdvice.findJwtMember();
        CalendarPost calendarPost = calendarService.createCalendarPost(new CreateCalendarPostDTO(
                jwtMember, req.calendarScheduleId(), req.title(), req.body()
        ));

        if (photos != null) {
            photoAdvice.saveCalendarPostPhoto(photos, calendarPost);
        }

        return new ResponseEntity<>(new CreateRes(calendarPost.getId()), HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 업데이트 API",
            description = "캘린더 게시글을 수정합니다. 해당 게시글을 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"
            ),
            @ApiResponse(responseCode = "404", description = "게시글 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/post/{id}/update")
    public ResponseEntity<?> updateCalendarPost(
            @PathVariable Long id,
            @RequestPart UpdateCalendarPostReq req,
            @RequestPart(required = false) List<MultipartFile> photos) throws IOException {

        CalendarPost calendarPost = calendarService.updateCalendarPost(new UpdateCalendarPostDTO(
                id,
                req.title(),
                req.body(),
                req.deletePhotoIdList()
        ));

        if (photos != null) {
            photoAdvice.saveCalendarPostPhoto(photos, calendarPost);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 삭제 API",
            description = "캘린더 게시글을 삭제합니다. 해당 게시글을 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"
            ),
            @ApiResponse(responseCode = "404", description = "게시글 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/post/{id}/delete")
    public ResponseEntity<?> deleteCalendarPost(@PathVariable Long id) {

        calendarService.deleteCalendarPost(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity<?> getPlan(@PathVariable Long id){

        CalendarPlanContentsInfoList result = calendarService.getCalendarPlan(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/plan/create")
    public ResponseEntity<?> createPlan(@RequestBody createCalendarPlanReq req){

        CalendarPlan calendarPlan = calendarService.createCalendarPlan(new CreateCalendarPlanDTO(
                req.body(),
                req.calendarScheduleId(),
                req.localDate()
        ));

        return new ResponseEntity<>(new CreateRes(calendarPlan.getId()), HttpStatus.OK);
    }

    @PostMapping("/plan/{id}/update")
    public ResponseEntity<?> updatePlan(@PathVariable Long id, @RequestBody UpdateCalendarPlanReq req){

        calendarService.updateCalendarPlan(new UpdateCalendarPlanDTO(
                id,
                req.body(),
                req.localDate()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/plan/{id}/delete")
    public ResponseEntity<?> deletePlan(@PathVariable Long id){

        calendarService.deleteCalendarPlan(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 일정 생성 API",
            description = "캘린더 일정을 생성합니다. 캘린더 태그의 ID가 올바르지 않으면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateRes.class))),
            @ApiResponse(responseCode = "404", description = "캘린더 태그 ID가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            ),
            @ApiResponse(responseCode = "400", description = "일정의 끝 날짜가 시작 날짜보다 작음"
            )
    })
    @PostMapping("/schedule/create")
    public ResponseEntity<?> createCalendarSchedule(CreateCalendarScheduleReq req) {

        if (req.endDate().isBefore(req.startDate())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Member jwtMember = memberAdvice.findJwtMember();

        CalendarSchedule calendarSchedule = calendarService.createCalendarSchedule(new CreateCalendarScheduleDTO(
                req.CalendarTagId(),
                req.title(),
                req.startDate(),
                req.endDate(),
                jwtMember
        ));

        return new ResponseEntity<>(new CreateRes(calendarSchedule.getId()), HttpStatus.OK);
    }

    @Operation(summary = "캘린더 일정 업데이트 API",
            description = "캘린더 일정을 수정합니다. 해당 일정을 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 수정 성공"
            ),
            @ApiResponse(responseCode = "404", description = "게시글 일정이 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            ),
            @ApiResponse(responseCode = "400", description = "일정의 끝 날짜가 시작 날짜보다 작음"
            )
    })
    @PostMapping("/schedule/{id}/update")
    public ResponseEntity<?> updateCalendarSchedule(
            @PathVariable Long id,
            UpdateCalendarScheduleReq req) {

        if (req.endDate().isBefore(req.startDate())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CalendarSchedule calendarSchedule = calendarService.updateCalendarSchedule(new UpdateCalendarScheduleDTO(
                id,
                req.title(),
                req.startDate(),
                req.endDate()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 일정 삭제 API",
            description = "캘린더 일정을 삭제합니다. 해당 일정을 찾지 못하면 404 코드를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 삭제 성공"
            ),
            @ApiResponse(responseCode = "404", description = "일정 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @PostMapping("/schedule/{id}/delete")
    public ResponseEntity<?> deleteCalendarSchedule(
            @PathVariable Long id
    ) {
        calendarService.deleteCalendarSchedule(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "캘린더 일정 조회 API",
            description = "캘린더 일정을 조회합니다. 해당 일정을 찾지 못하면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarScheduleDetailInfo.class))),
            @ApiResponse(responseCode = "404", description = "일정 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @GetMapping("schedule/{id}")
    public ResponseEntity<?> getCalendarSchedule(@PathVariable Long id) {

        CalendarScheduleDetailInfo calendarScheduleDetail = calendarService.getCalendarScheduleDetail(id);

        return new ResponseEntity<>(calendarScheduleDetail, HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 코멘트 생성 API",
            description = "캘린더 게시글 코멘트를 생성합니다. 캘린더 게시글의 ID가 올바르지 않으면 404 에러를 반환합니다.",
            parameters = @Parameter(name = "postId", description = "코멘트가 생성될 게시글 id")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 코멘트 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateRes.class))),
            @ApiResponse(responseCode = "404", description = "게시글 ID가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))),


    })
    @PostMapping("/post/{postId}/commentcreate")
    public ResponseEntity<?> createCalendarPostComment(
            @PathVariable Long postId,
            @RequestBody CCPCReq req) {

        Member jwtMember = memberAdvice.findJwtMember();
        CalendarPostComment calendarPostComment = calendarService.createCalendarPostComment(new CCPCDTO(
                req.body(), jwtMember, postId
        ));

        return new ResponseEntity<>(new CreateRes(calendarPostComment.getId()), HttpStatus.OK);
    }

    @Operation(summary = "캘린더 게시글 코멘트 업데이트 API",
            description = "캘린더 게시글 코멘트를 수정합니다. 해당 코멘트가 존재하지 않으면 404 에러를 반환합니다.",
            parameters = @Parameter(name = "commentId", description = "수정될 코멘트 id")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코멘트 수정 성공"),
            @ApiResponse(responseCode = "404", description = "코멘트 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))),
            @ApiResponse(responseCode = "400", description = "코멘트 본문이 없음"),
            @ApiResponse(responseCode = "401", description = "본인이 작성한 코멘트 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class)))
    })
    @PostMapping("/comment/{commentId}/update")
    public ResponseEntity<?> updateCalendarPostComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCPCReq req) throws IOException {

        try {
            CalendarPostComment calendarPostComment = calendarService.updateCalendarPostComment(new UpdateCPCDTO(
                    commentId,
                    req.body()
            ));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundEntityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "캘린더 게시글 코멘트 삭제 API",
            description = "캘린더 게시글 코멘트을 삭제합니다. 해당 코멘트를 찾지 못하면 404 코드를 반환합니다.",
            parameters = @Parameter(name = "commentId", description = "삭제될 코멘트 id"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코멘트 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "코멘트 아이디가 잘못됨",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))),
            @ApiResponse(responseCode = "401", description = "본인이 작성한 코멘트 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class)))
    })
    @PostMapping("/comment/{commentId}/delete")
    public ResponseEntity<?> deleteCalendarPostComment(@PathVariable Long commentId) {
        try {
            calendarService.deleteCalendarPostComment(commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundEntityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "캘린더 게시글 코멘트 조회 API",
            description = "캘린더 게시글의 코멘트를 조회합니다.",
            parameters = {
                    @Parameter(name = "postId", description = "게시글 id"),
                    @Parameter(name = "cursorId", description = "다음 스크롤의 id, 미입력 시 첫 페이지에 해당되는 코멘트 반환"),
                    @Parameter(name = "sort", description = "오름차순일 경우 asc, 내림차순일 경우 desc(미입력 시 기본값)"),
                    @Parameter(name = "size", description = "한 스크롤 당 반환되는 코멘트 수(기본값: 20)")
            })
    @GetMapping("/post/{postId}/comments")
    public CursorResult<CPCRes> getComments(@PathVariable Long postId,
                                            @RequestParam(value = "cursorId", required = false) Long cursorId,
                                            @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort,
                                            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        return calendarService.getCalendarPostComments(postId, cursorId, sort, PageRequest.of(0, size));
    }

    @Operation(summary = "품품로그 조회 API",
            description = "품품로그를 일정의 내림차순으로 조회합니다. 쿼리스트링을 입력하지 않을 경우, 사이드바의 품품로그에 필요한 만큼 반환됩니다.",
            parameters = {
                    @Parameter(name = "cursorId", description = "다음 스크롤의 id, 미입력 시 첫 페이지에 해당되는 품품로그 반환.(0이 아닌 1부터 시작)"),
                    @Parameter(name = "size", description = "한 스크롤 당 반환되는 품품로그 수(기본값: 6)")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "페이지 번호(cursorId)가 잘못됨.",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class)))
    })
    @GetMapping("/poompoomlog")
    public CursorResult<PoomPoomLogInfo> getPoomPoomLogs(@RequestParam(value = "cursorId", defaultValue = "1") int cursorId,
                                                            @RequestParam(value = "size", defaultValue = "6") Integer size){
        if (cursorId <= 0) {
            throw new PageException("페이지 번호는 0보다 커야 합니다");
        }
        return calendarService.getPoomPoomLogs(cursorId, size);
    }

    @Operation(summary = "월별 품품로그 조회 API",
            description = "선택한 월의 품품로그를 일정의 오름차순으로 조회합니다.",
            parameters = {
                    @Parameter(name = "month", description = "월별 품품로그를 조회할 월")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "월(month)의 값이 잘못됨.",
                    content = @Content(schema = @Schema(implementation = ErrorResult.class)))
    })
    @GetMapping("/poompoomlog/monthly")
    public List<PoomPoomLogInfo> getMonthlyPoomPoomLogs(@RequestParam(value = "month") int month){
        if (month < 1 || month > 12) {
            throw new InvalidMonthException("월(month)은 1~12의 값만 허용합니다.");
        }
        return calendarService.getMonthlyPoomPoomLogs(month);
    }
}
