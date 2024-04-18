package towssome.server.dto;

import java.util.List;

public record VoteReq(
        String title,
        List<VoteAttributeReq> voteAttributeList
) {
}
