package towssome.server.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.entity.RefreshToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static towssome.server.jwt.JwtStatic.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletResponse response, HttpServletRequest request) throws IOException {

        // 리프레쉬 토큰 get
        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새 JWT 발급
        String newAccess = jwtUtil.createJwt("access", username, role, ACCESS_EXPIRE_MS);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_EXPIRE_MS);

        if (refresh.equals(newRefresh)) {
            System.out.println("리프레쉬 값이 같습니다");
        } else {
            System.out.println("리프레쉬 값이 다릅니다");
            log.info("refresh = {}", refresh);
            log.info("new refresh = {}", newRefresh);
        }

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        // response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print("{\"access\":\"" + newAccess + "\", \"refresh\":\"" + newRefresh + "\"}");
        out.flush();

        response.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
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
