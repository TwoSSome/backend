package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.PageResult;
import towssome.server.dto.SubscribeRes;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;
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

    @Operation(summary = "구독 추가 API")
    @PostMapping("/add")
    public ResponseEntity<?> addSubscribe(@RequestBody Long followingMember){

        Member subscriber = memberAdvice.findJwtMember();
        Member following = memberService.getMember(followingMember);

        subscribeService.addSubscribe(subscriber, following);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "구독 취소 API")
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscribe(@RequestBody Long cancelSubscribeId){

        Subscribe cancelSubscribe = subscribeService.getSubscribe(cancelSubscribeId);
        subscribeService.cancelSubscribe(cancelSubscribe);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //페이지네이션
    @Operation(summary = "구독 계정 조회 API")
    @GetMapping
    public PageResult<SubscribeRes> getSubscribe(
            @RequestParam int page,
            @RequestParam int size){

        Member jwtMember = memberAdvice.findJwtMember();
        return subscribeService.getSubscribePage(jwtMember, page, size);
    }

}
