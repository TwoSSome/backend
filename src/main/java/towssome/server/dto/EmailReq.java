package towssome.server.dto;

import jakarta.validation.constraints.Email;

public record EmailReq(
        @Email
        String email
) {
}
