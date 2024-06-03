package towssome.server.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import towssome.server.dto.ErrorResult;
import towssome.server.entity.RefreshToken;
import towssome.server.repository.MemberRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static towssome.server.jwt.JwtStatic.*;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("username = {}",username);

        //스프링 시큐리티에서 username 과 password 를 검증하기 위해서는 token 에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //token 에 담긴 검증을 위한 토큰을 AuthenticationManager 로 전달
        try {
            return authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            // 잘못된 자격 증명 예외 처리
            if (!userExist(username)) {
                throw new CustomAuthenticationException("존재하지 않는 id입니다");
            } else {
                throw new CustomAuthenticationException("비밀번호가 틀립니다");
            }
        }
    }

    private boolean userExist(String username) {
        return memberRepository.existsByUsername(username);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT 를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //유저 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRE_MS);
        String refresh = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRE_MS);

        //refreshToken 저장
        addRefreshEntity(username,refresh,REFRESH_EXPIRE_MS);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh)); //refresh token 은 쿠키로
        response.setStatus(HttpStatus.OK.value());

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResult errorResponse = new ErrorResult("UNAUTHORIZED EXCEPTION", failed.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }

    //쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(false); //https 설정 시
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshEntity = new RefreshToken(username,refresh,date.toString());

        refreshTokenRepository.save(refreshEntity);
    }
}
