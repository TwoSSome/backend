package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.repository.VoteAttributeMemberRepository;
import towssome.server.repository.VoteAttributeRepository;
import towssome.server.repository.VoteRepository;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteAttributeRepository voteAttributeRepository;
    private final VoteAttributeMemberRepository voteAttributeMemberRepository;




}
