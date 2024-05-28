package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CreateRes;
import towssome.server.dto.ErrorResult;
import towssome.server.entity.Member;
import towssome.server.jwt.JoinDTO;
import towssome.server.jwt.JoinService;
import towssome.server.service.MemberService;

@Tag(name = "회원")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JoinService joinService;
    private final MemberAdvice memberAdvice;

    @Operation(summary = "회원가입 API", description = "회원가입 성공시 가입된 멤버의 id 반환",
    parameters = {@Parameter(name = "req", description = "username(ID), password, nickname, 프로필 태그들의 id / json으로 보내야 함")},
    responses = {@ApiResponse(responseCode = "400", description = "중복된 id 가입 시도", content = @Content(schema = @Schema(implementation = ErrorResult.class))),
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = CreateRes.class)))})
    @PostMapping("/member/join")
    public ResponseEntity<CreateRes> joinMember(
            @RequestPart JoinDTO req,
            @RequestPart(required = false)
            MultipartFile profileImage) {
        Member member = joinService.joinProcess(req, profileImage);
        return new ResponseEntity<>(new CreateRes(
                member.getId()
        ),HttpStatus.OK);
    }

    @Operation(summary = "테스트용 API")
    @GetMapping("/auth")
    public String  auth(){
        //로그인한 유저의 username 을 찾기 위해서는 다음 유틸 메소드 사용
        //만약 비회원도 사용 가능하게 만들면, 다음 메소드의 결과는 anonymousUser 로 획득할 수 있습니다
        Member member = memberAdvice.findJwtMember();
        log.info("username = {}",member.getUsername());
        return "auth ok";
    }

    @Operation(summary = "테스트용 API")
    @GetMapping("/testAuth")
    public String testAuth (){

        Member jwtMember = memberAdvice.findJwtMember();
        if (jwtMember == null) return "null member";
        else return jwtMember.getNickName();
    }

}
