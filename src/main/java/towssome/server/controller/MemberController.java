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
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.exception.DuplicateIdException;
import towssome.server.jwt.JoinDTO;
import towssome.server.service.JoinService;
import towssome.server.service.MemberService;

@Tag(name = "회원")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JoinService joinService;
    private final MemberAdvice memberAdvice;

    @Operation(summary = "이메일 인증 요청 API",parameters = {@Parameter(name = "email", description = "인증할 이메일")},
    responses = {
            @ApiResponse(responseCode = "500", description = "이메일 형식이 올바르지 않음"),
            @ApiResponse(responseCode = "200", description = "이메일 정상 발송")
    })
    @PostMapping("/{email}/send")
    public ResponseEntity<?> emailVerificationReq(@PathVariable String email){
        joinService.sendEmailVerification(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "이메일 인증번호 체크 요청 API", description = "인증 요청 API를 먼저 보내야 합니다",
            responses = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 인증번호 다름"),
            @ApiResponse(responseCode = "400", description = "중복된 이메일 가입 시도", content = @Content(schema = @Schema(implementation = ErrorResult.class)))
    }, parameters = {
            @Parameter(name = "email", description = "인증할 이메일"),
            @Parameter(name = "req", description = "입력한 인증번호")
    })
    @PostMapping("/{email}/check")
    public ResponseEntity<?> emailCheck(@PathVariable String email, @RequestBody EmailCheckReq req){
        if (joinService.verificationJoinEmail(email, req.authNum())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

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

    @Operation(summary = "아이디 중복체크 API", description = "중복되지 않으면 200 코드 반환, 중복되어있으면 DuplicateIdException 반환")
    @PostMapping("/member/dupidcheck")
    public ResponseEntity<?> duplicateIdCheck(@RequestBody IdDupCheckReq req){

        if (memberService.dupIdCheck(req.username())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new DuplicateIdException("이미 가입된 아이디입니다");
        }

    }

    @Operation(summary = "아이디 찾기 API", description = "이메일이 정상 발송되면 200 코드 반환, 발송되지 않으면 EmailSendException 반환")
    @PostMapping("/member/findId")
    public ResponseEntity<?> findMyUsername(@RequestBody EmailReq req){
        memberService.findMyUsername(req.email());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정 요청 API", description = "이메일이 정상 발송되면 200 코드 반환")
    @PostMapping("/member/reconfig_password/req")
    public ResponseEntity<?> ReconfigMyPassword(@RequestBody EmailUsernameReq req){
        memberService.reconfigPassword(req.email(),req.username());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정 인증번호 API",
            description = "인증번호가 맞으면 200코드 반환, 틀리면 401 반환, 인증번호가 만료되면 ExpirationEmailException 발생")
    @PostMapping("/member/reconfig_password/authenticate")
    public ResponseEntity<?> ReconfigPasswordAuthenticate(@RequestBody EmailAuthNumReq req){
        boolean checked = memberService.checkReconfigPasswordAuthNum(req.authNum(), req.email());
        if (checked) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
