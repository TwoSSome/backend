package towssome.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.advice.MemberAdvice;
import towssome.server.service.VoteService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
@Tag(name = "****DEPRECATED****(투표)", description = "투표 기능")
public class VoteController {

    private final VoteService voteService;
    private final MemberAdvice memberAdvice;

    @PostMapping("/add")
    public ResponseEntity<?> addVote(@RequestBody Long id){

        voteService.doVote(memberAdvice.findJwtMember(),voteService.getVoteAttribute(id));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelVote(@RequestBody Long id){

        voteService.cancelVote(memberAdvice.findJwtMember(),voteService.getVoteAttribute(id));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
