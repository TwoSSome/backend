package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.MatingRes;
import towssome.server.entity.Mating;
import towssome.server.entity.Member;
import towssome.server.service.MatingService;
import towssome.server.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mating")
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
    public ResponseEntity<?> createOffer(@RequestBody Long memberId){

        Member jwtMember = memberAdvice.findJwtMember();
        matingService.createOffer(jwtMember, memberService.getMember(memberId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptOffer(@RequestBody Long matingId){

        matingService.acceptOffer(matingService.findById(matingId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectOffer(@RequestBody Long matingId){

        matingService.rejectOffer(matingService.findById(matingId));

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
