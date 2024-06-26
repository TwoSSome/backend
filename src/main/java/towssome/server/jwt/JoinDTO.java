package towssome.server.jwt;

import java.util.List;

public record JoinDTO(
        String username,
        String password,
        String nickname,
        List<String> profileTagNames
) {
}
