package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CalendarTag;
import towssome.server.entity.Member;
import towssome.server.service.CalendarServiceInterface;

@Tag(name = "캘린더")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarServiceInterface calendarService;
    private final MemberAdvice memberAdvice;

    @GetMapping("/")
    public ResponseEntity<?> getCalendarInfo() {

        return null;
    }

    @Operation(summary = "태그 조회 API",
            description = "태그를 조회합니다. 해당 태그를 찾지 못하면 404 에러를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 생성 성공",
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
    public ResponseEntity<?> updateCalendarTag(@PathVariable Long id ,UpdateCalendarTagReq req) {

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
    public ResponseEntity<?> deleteCalendarTag(@PathVariable Long id){

        calendarService.deleteCalendarTag(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
