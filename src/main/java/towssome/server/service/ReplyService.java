package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ReplyRes;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Member;
import towssome.server.entity.Reply;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundReplyException;
import towssome.server.repository.ReplyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    /**
     * 답변글 생성
     * @param body
     * @param quotation
     * @param author
     * @param communityPost
     * @param isAnonymous
     * @return
     */
    public Reply createReply(
            String body,
            ReviewPost quotation,
            Member author,
            CommunityPost communityPost,
            Boolean isAnonymous) {
        return replyRepository.save(new Reply(
                body,
                quotation,
                communityPost,
                author,
                isAnonymous
        ));
    }

    public Reply findReply(Long id) {
        return replyRepository.findById(id).orElseThrow(
                () -> new NotFoundReplyException("해당 답변글이 없습니다 !!")
        );
    }

    @Transactional
    public Reply updateReply(Reply reply, String body, ReviewPost quotation) {
        reply.update(body, quotation);
        return reply;
    }

    @Transactional
    public void adoptionReply(Reply reply) {
        reply.setAdoption(true);
    }

    @Transactional
    public void adoptionReply(Long id) {
        Reply reply = replyRepository.findById(id).orElseThrow();
        reply.setAdoption(true);
    }

    public void deleteReply(Reply reply) {
        replyRepository.delete(reply);
    }

    public List<Reply> findPostAllReplies(CommunityPost communityPost) {
        return replyRepository.findAllByCommunityPost(communityPost);
    }

    /**
     * 커뮤니티글의 답변글을 무한스크롤로 구현하기 위해 슬라이스 반환
     * @param communityPost
     * @param page
     * @param member
     * @return
     */
    @Transactional
    public CursorResult<ReplyRes> findPostReplySlice(CommunityPost communityPost, int page, Member member) {
        Slice<Reply> slice = replyRepository.findAllByCommunityPost(communityPost,
                PageRequest.of(page, 10, Sort.Direction.ASC, "createDate")); //일찍 생성된 순으로
        List<Reply> content = slice.getContent();

        ArrayList<ReplyRes> replyRes = new ArrayList<>();
        for (Reply reply : content) {

            Long quotationId = reply.getQuotation() == null ? null : reply.getQuotation().getId(); //인용된 리뷰글이 없다면

            replyRes.add(new ReplyRes(
                    reply.getId(),
                    reply.getBody(),
                    reply.isAnonymous(),
                    member.getUsername().equals(reply.getAuthor().getUsername()),
                    reply.isAdoption(),
                    quotationId,
                    reply.getAuthor().getId(),
                    reply.getAuthor().getNickName()
            ));
        }

        return new CursorResult<>(
                replyRes,
                (long) page+1,
                slice.hasNext()
        );
    }

    public List<Reply> findMemberAllReplies(Member author) {
        return replyRepository.findAllByAuthor(author);
    }


}
