package towssome.server.dto;

import java.util.List;

public record VoteSaveReq(
        String title,
        List<VoteAttributeReq> voteAttributeReqs
) {
}
