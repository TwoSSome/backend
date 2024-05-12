package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.advice.MemberAdvice;
import towssome.server.entity.Member;
import towssome.server.jwt.JoinDTO;
import towssome.server.jwt.JoinService;
import towssome.server.service.MemberService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JoinService joinService;
    private final MemberAdvice memberAdvice;

    @PostMapping("/member/join")
    public ResponseEntity<String> joinMember(@RequestBody JoinDTO req) {
        joinService.joinProcess(req);
        return new ResponseEntity<String>("Join Complete",HttpStatus.OK);
    }

    @GetMapping("/auth")
    public String  auth(){
        //로그인한 유저의 username 을 찾기 위해서는 다음 유틸 메소드 사용
        //만약 비회원도 사용 가능하게 만들면, 다음 메소드의 결과는 anonymousUser 로 획득할 수 있습니다
        Member member = memberAdvice.findJwtMember();
        log.info("username = {}",member.getUsername());
        return "auth ok";
    }

}
