package towssome.server.dto;

import java.util.List;

public record UpdateProfileReq(
        String nickName,
        List<String> profileTag
) {
}
