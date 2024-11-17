package towssome.server.dto;

public record EmailAuthNumReq(
        String email,
        int authNum
) {
}
