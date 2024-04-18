package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.CommunityPostSaveDTO;
import towssome.server.dto.CommunityPostUpdateDto;
import towssome.server.entity.CommunityPost;
import towssome.server.exception.NotFoundCommunityPostException;
import towssome.server.repository.CommunityPostRepository;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;

    public Long create(CommunityPostSaveDTO dto) {
        CommunityPost communityPost = new CommunityPost(
                dto.title(),
                dto.body(),
                dto.member(),
                dto.reviewPost()
        );
        communityPostRepository.save(communityPost);
        return communityPost.getId();
    }

    public CommunityPost findPost(Long id) {
        return communityPostRepository.findById(id).orElseThrow(() ->
                new NotFoundCommunityPostException("해당 커뮤니티 글이 존재하지 않습니다"));
    }

    public void updatePost(CommunityPostUpdateDto dto) {
        CommunityPost post = findPost(dto.id());
        post.update(
                dto.title(),
                dto.body(),
                dto.quotation()
        );
    }

    public void deletePost(CommunityPost post) {
        communityPostRepository.delete(post);
    }

}
