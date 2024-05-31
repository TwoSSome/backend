package towssome.server.dto;

import jakarta.validation.constraints.Email;

public record EmailCheckReq(
        int authNum
) {
}
