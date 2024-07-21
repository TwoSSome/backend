package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.MatingIdReq;
import towssome.server.dto.MatingRes;
import towssome.server.dto.MemberNameReq;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;
import towssome.server.service.MatingService;
import towssome.server.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mating")
@Slf4j
public class MatingController {

    private final MatingService matingService;
    private final MemberAdvice memberAdvice;
    private final MemberService memberService;

    @GetMapping
    public MatingRes getMate(){

        Member jwtMember = memberAdvice.findJwtMember();
        Mating myMating = matingService.findMyMating(jwtMember);

        return new MatingRes(
                myMating.getId(),
                myMating.getStatus()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOffer(@RequestBody MemberNameReq req){

        Member jwtMember = memberAdvice.findJwtMember();
        matingService.createOffer(jwtMember, memberService.getMember(req.username()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptOffer(@RequestBody MatingIdReq req){

        matingService.acceptOffer(matingService.findById(req.matingId()));
        log.info("accept cont");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectOffer(@RequestBody MatingIdReq req){

        matingService.rejectOffer(matingService.findById(req.matingId()));

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
