package towssome.server.dto;

import java.util.List;

public record VoteAttributeRes(
        String title,
        List<VoteMember> voteMembers,
        String s3PhotoPath,
        Long count
) {
}
