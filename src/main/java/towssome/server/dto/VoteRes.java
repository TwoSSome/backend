package towssome.server.dto;

import java.util.List;

public record VoteRes(
        String title,
        List<VoteAttributeRes> voteAttributeRes
) {
}
