package towssome.server.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import towssome.server.advice.RoleAdvice;
import towssome.server.entity.Member;
import towssome.server.entity.RefreshToken;
import towssome.server.jwt.JwtUtil;
import towssome.server.jwt.RefreshTokenRepository;
import towssome.server.repository.member.MemberRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static towssome.server.jwt.JwtStatic.ACCESS_EXPIRE_MS;
import static towssome.server.jwt.JwtStatic.REFRESH_EXPIRE_MS;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    @Value("${social.local.url}")
    private String localURL;
    @Value("${social.ec2.url}")
    private String ec2URL;
    @Value("${social.tempuser}")
    private String tempURL;
    @Value("${social.user}")
    private String userURL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getName();
        log.info("login success");

        Member member = memberRepository.findBySocialId(username).orElseThrow();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt("social", username, role, ACCESS_EXPIRE_MS);
        log.info("USER ROLE = {}", role);

        String redirectUrl = null;

        if (role.equals(RoleAdvice.ROLE_TEMP)) { // 처음 로그인 했을 때
            redirectUrl = localURL + tempURL; // 초기 설정 페이지로 리다이렉트
        } else { // 처음 로그인이 아닐 때
            redirectUrl = localURL + userURL;
        }
        String redirectUrlWithToken = String.format("%s?token=%s", redirectUrl, token); // jwt 토큰 포함
        response.sendRedirect(redirectUrlWithToken);



//        String host = request.getHeader("Referer");

//        if (host.contains("localhost")) {
//            // 로컬 환경에서 사용할 때
//            log.info("host = {}",host);
//            if (!role.equals(RoleAdvice.ROLE_TEMP)) { // 처음 로그인 했을 때
//                String redirectUrl = localURL + tempURL; // 초기 설정 페이지로 리다이렉트
//                String redirectUrlWithToken = String.format("%s?token=%s", redirectUrl, token); // jwt 토큰 포함
//                response.sendRedirect(redirectUrlWithToken);
//            } else { // 처음 로그인이 아닐 때
//                String redirectUrl = localURL;
//                String redirectUrlWithToken = String.format("%s?token=%s", redirectUrl, token);
//                response.sendRedirect(redirectUrlWithToken);
//            }
//        }else {
//            // ec2 환경에서 사용할 때
//            if (role.equals(RoleAdvice.ROLE_TEMP)) {
//                String redirectUrl = ec2URL + tempURL;
//                String redirectUrlWithToken = String.format("%s?token=%s", redirectUrl, token);
//                response.sendRedirect(redirectUrlWithToken);
//            } else {
//                String redirectUrl = ec2URL;
//                String redirectUrlWithToken = String.format("%s?token=%s", redirectUrl, token);
//                response.sendRedirect(redirectUrlWithToken);
//            }
//        }


    }

    private void tokenResponse(HttpServletResponse response, String username, String role, Member member) throws IOException {
        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRE_MS);
        String refresh = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRE_MS);

        // refreshToken 저장
        addRefreshEntity(username, refresh, REFRESH_EXPIRE_MS);

        // 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형식으로 반환
        PrintWriter out = response.getWriter();
        out.print("{\"access\":\"" + access + "\", \"refresh\":\"" + refresh + "\", \"memberId\":" + member.getId() + "}");
        out.flush();

        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        if (refreshTokenRepository.existsByUsername(username)) {
            RefreshToken byUsername = refreshTokenRepository.findByUsername(username);
            refreshTokenRepository.delete(byUsername);
        }

        RefreshToken refreshEntity = new RefreshToken(username,refresh,date.toString());

        refreshTokenRepository.save(refreshEntity);
    }


}
