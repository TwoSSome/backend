package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import towssome.server.entity.Comment;
import towssome.server.entity.CommentLike;
import towssome.server.entity.Member;
import towssome.server.repository.comment_like.CommentLikeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    public Boolean isLikedComment(Member member, Comment comment){
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId());
        if(commentLike == null) return false;
        else return true;
    }

    public Long countLike(Comment comment){
        return commentLikeRepository.countByCommentId(comment.getId());
    }

    public void likeProcess(Member member, Comment comment){
        CommentLike commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId());
        if(commentLike == null){
            commentLikeRepository.save(new CommentLike(member,comment));
        }
        else {
            commentLikeRepository.delete(commentLike);
        }
    }
}
