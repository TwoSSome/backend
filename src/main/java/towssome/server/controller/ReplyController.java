package towssome.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Member;
import towssome.server.entity.Reply;
import towssome.server.exception.PageException;
import towssome.server.service.CommunityService;
import towssome.server.service.ReplyService;
import towssome.server.service.ReviewPostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
@Tag(name = "****DEPRECATED****(답변)", description = "답글 기능")
public class ReplyController {

    private final ReplyService replyService;
    private final CommunityService communityService;
    private final MemberAdvice memberAdvice;
    private final ReviewPostService reviewPostService;

    @GetMapping("/{id}")
    public CursorResult<ReplyRes> getPostReply(
            @PathVariable Long id,
            @RequestParam int page){

        if(page <= 0) throw new PageException("1 이상의 페이지를 입력하세요");

        Member jwtMember = memberAdvice.findJwtMember();
        CommunityPost post = communityService.findPost(id);

        return replyService.findPostReplySlice(post, page - 1, jwtMember);
    }

    @PostMapping("/adopt")
    public ResponseEntity<?> adoptReply(@RequestBody Long id){

        replyService.adoptionReply(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateRes> createReply(@RequestBody CreateReplyReq req){
        Member jwtMember = memberAdvice.findJwtMember();

        Reply reply = replyService.createReply(
                req.body(),
                reviewPostService.getReview(req.quotationId()),
                jwtMember,
                communityService.findPost(req.postId()),
                req.isAnonymous());

        return new ResponseEntity<>(new CreateRes(reply.getId()),HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<CreateRes> updateReply(@RequestBody UpdateReplyReq req){

        Reply reply = replyService.updateReply(
                replyService.findReply(req.id()),
                req.body(),
                reviewPostService.getReview(req.quotationId()));

        return new ResponseEntity<>(new CreateRes(reply.getId()),HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteReply(@RequestBody Long id){

        replyService.deleteReply(replyService.findReply(id));

        return new ResponseEntity<>(HttpStatus.OK);
    }




}
