package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "연인",description = "연인 관계 신청, 수락, 거절 등을 사용하는 API")
public class MatingController {

    private final MatingService matingService;
    private final MemberAdvice memberAdvice;
    private final MemberService memberService;

    @Operation(summary = "연인 신청 확인 API", description = "내가 전송한 연인 신청이나 받은 연인 요청을 확인합니다, AT 필요")
    @GetMapping
    public MatingRes getMate(){

        Member jwtMember = memberAdvice.findJwtMember();
        Mating myMating = matingService.findMyMating(jwtMember);

        return new MatingRes(
                myMating.getId(),
                myMating.getStatus()
        );
    }

    @Operation(summary = "연인 신청 API", description = "상대방에게 연인 신청을 보냅니다, AT 필요")
    @PostMapping("/create")
    public ResponseEntity<?> createOffer(@RequestBody MemberNameReq req){

        Member jwtMember = memberAdvice.findJwtMember();
        matingService.createOffer(jwtMember, memberService.getMember(req.username()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "연인 수락 API", description = "연인 신청을 수락합니다")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptOffer(@RequestBody MatingIdReq req){

        matingService.acceptOffer(matingService.findById(req.matingId()));
        log.info("accept cont");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "연인 거절 API", description = "연인 신청을 거절합니다")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectOffer(@RequestBody MatingIdReq req){

        matingService.rejectOffer(matingService.findById(req.matingId()));

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
