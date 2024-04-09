package towssome.server.dto;

import lombok.Data;

public record JoinReq(
        String memberUsingId,
        String passwd,
        String nickName
) {
}
