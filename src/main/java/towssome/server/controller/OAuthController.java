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
import towssome.server.dto.SocialLoginSuccessRes;
import towssome.server.dto.OAuthInitialConfigReq;
import towssome.server.entity.Member;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.jwt.JwtStatic;
import towssome.server.jwt.JwtUtil;
import towssome.server.repository.member.MemberRepository;
import towssome.server.service.JoinService;
import towssome.server.service.MemberService;

@Tag(name = "소셜 로그인")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JoinService joinService;

    @Operation(summary = "소셜 로그인 API", description = "소셜 로그인 후 받은 jwt를 이 API로 보내 AT와 RT를 획득할 수 있습니다",
            parameters = @Parameter(name = "jwt", description = "소셜로그인 시 전달받은 jwt"),
            responses = {
                    @ApiResponse(responseCode = "400", description = "jwt 형식이 올바르지 않음"),
                    @ApiResponse(responseCode = "200", description = "jwt 인증 성공", content = @Content(schema = @Schema(implementation = SocialLoginSuccessRes.class)))
            })
    @GetMapping("/OAuth/social")
    public ResponseEntity<?> oauthJwt(@RequestParam String jwt) {

        String category = jwtUtil.getCategory(jwt);
        String socialId = jwtUtil.getUsername(jwt);
        String role = jwtUtil.getRole(jwt);

        // 올바르지 않은 jwt일 경우
        if (!category.equals("social") || jwtUtil.isExpired(jwt)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Member member = memberRepository.findBySocialId(socialId).orElseThrow(() -> new NotFoundMemberException("해당 멤버가 없습니다"));

        String access = jwtUtil.createJwt("access", member.getUsername(), role, JwtStatic.ACCESS_EXPIRE_MS);
        String refresh = jwtUtil.createJwt("refresh", member.getUsername(), role, JwtStatic.REFRESH_EXPIRE_MS);

        return new ResponseEntity<>(
                new SocialLoginSuccessRes(
                        access,
                        refresh,
                        member.getId(),
                        member.getUsername(),
                        member.getNickName()
                ), HttpStatus.OK
        );
    }

    @Operation(summary = "최초 소셜 로그인 API", description = "최초 소셜 로그인 시 닉네임과 프로필 사진을 설정하는 API")
    @PostMapping("/OAuth/initial")
    public ResponseEntity<?> initial(
            @RequestPart OAuthInitialConfigReq req,
            @RequestPart(required = false) MultipartFile profileImage) {

        log.info("OAuth/initial");
        String jwt = req.jwt();

        String category = jwtUtil.getCategory(jwt);
        String socialId = jwtUtil.getUsername(jwt);
        log.info("socialId = {}", socialId);

        // 올바르지 않은 jwt일 경우
        if (!category.equals("social") || jwtUtil.isExpired(jwt)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Member member = memberRepository.findBySocialId(socialId).orElseThrow(() -> new NotFoundMemberException("존재하지 않는 social 회원입니다"));

        if (profileImage != null && !profileImage.isEmpty()) {
            memberService.changeProfilePhoto(member, profileImage);
        }

        joinService.socialJoinProcess(member, req.username(), req.nickname(), req.profileTags());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
