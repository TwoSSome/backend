package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.SubscribeRes;
import towssome.server.dto.SubscribeSlice;
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

    @PostMapping("/add")
    public ResponseEntity<?> addSubscribe(@RequestBody Long followingMember){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Member subscriber = memberService.getMemberUsername(name);
        Member following = memberService.findMember(followingMember);

        subscribeService.addSubscribe(subscriber, following);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscribe(@RequestBody Long cancelSubscribeId){

        Subscribe cancelSubscribe = subscribeService.getSubscribe(cancelSubscribeId);
        subscribeService.cancelSubscribe(cancelSubscribe);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //페이징?
    @GetMapping()
    public SubscribeSlice getSubscribe(
            @RequestParam int page,
            @RequestParam int offset){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();


        return null;
    }

}
