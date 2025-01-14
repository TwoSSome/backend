package towssome.server.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // HTTP 상태 코드 403 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 응답 Content-Type 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 에러 메시지 생성
        String errorMessage = "{\"error\": \"Forbidden\", \"message\": \"access token이 필요합니다\"}";

        // 응답 메시지 쓰기
        PrintWriter writer = response.getWriter();
        writer.write(errorMessage);
        writer.flush();
    }
}
