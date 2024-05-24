package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.SubscribeRes;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;
import towssome.server.service.MemberService;
import towssome.server.service.SubscribeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;
    private final MemberService memberService;
    private final MemberAdvice memberAdvice;

    @PostMapping("/add")
    public ResponseEntity<?> addSubscribe(@RequestBody Long followingMember){

        Member subscriber = memberAdvice.findJwtMember();
        Member following = memberService.getMember(followingMember);

        subscribeService.addSubscribe(subscriber, following);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscribe(@RequestBody Long cancelSubscribeId){

        Subscribe cancelSubscribe = subscribeService.getSubscribe(cancelSubscribeId);
        subscribeService.cancelSubscribe(cancelSubscribe);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //페이지네이션
    @GetMapping
    public PageResult<SubscribeRes> getSubscribe(
            @RequestParam int page,
            @RequestParam int size){

        Member jwtMember = memberAdvice.findJwtMember();
        return subscribeService.getSubscribePage(jwtMember, page, size);
    }

}
