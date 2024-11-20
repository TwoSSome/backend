package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;
import towssome.server.exception.PageException;
import towssome.server.service.MemberService;
import towssome.server.service.SubscribeService;

@Tag(name = "구독")
@RestController
@RequiredArgsConstructor
@RequestMapping("/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;
    private final MemberService memberService;
    private final MemberAdvice memberAdvice;

    @Operation(summary = "구독 추가 API", parameters = {
    }, responses = {
            @ApiResponse(responseCode = "200", description = "구독 성공", content = @Content(schema = @Schema(implementation = CreateRes.class)))
    })
    @PostMapping("/add")
    public ResponseEntity<?> addSubscribe(@RequestBody FollowReq req) {

        Member subscriber = memberAdvice.findJwtMember();
        Member following = memberService.getMember(req.subscribeId());

        Subscribe subscribe = subscribeService.addSubscribe(subscriber, following);

        return new ResponseEntity<>(new CreateRes(subscribe.getId()), HttpStatus.OK);
    }

    @Operation(summary = "구독 취소 API", parameters = {
    })
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscribe(@RequestBody CancelFollowReq req) {

        Subscribe cancelSubscribe = subscribeService.getSubscribe(req.cancelId());
        subscribeService.cancelSubscribe(cancelSubscribe);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //페이지네이션
    @Operation(summary = "구독 계정 조회 API", parameters = {
            @Parameter(name = "page", description = "1부터 시작"),
            @Parameter(name = "size", description = "기본값 10")
    })
    @GetMapping
    public CursorResult<SubscribeRes> getSubscribe(
            @RequestParam int cursorId,
            @RequestParam(required = false) Integer size) {

        if (cursorId < 1) {
            throw new PageException("페이지는 1보다 작을 수 없습니다!!");
        }

        if (size == null) size = 10;
        Member jwtMember = memberAdvice.findJwtMember();
        return subscribeService.getSubscribePage(jwtMember, cursorId - 1, size);
    }

}
