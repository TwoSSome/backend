package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.CommunityPostRes;
import towssome.server.dto.CommunityPostSaveReq;
import towssome.server.dto.CommunityPostUpdateReq;
import towssome.server.dto.PhotoInPost;
import towssome.server.entity.CommunityPost;
import towssome.server.repository.ReviewPostRepository;
import towssome.server.service.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final Long testMemberId = 1L;

    private final CommunityService communityService;
    private final ReviewPostRepository reviewPostRepository;
    private final MemberService memberService;
    private final PhotoService photoService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody CommunityPostSaveReq req) throws IOException {

        Long createdPost = communityService.create(
                new CommunityPostSaveDTO(
                        req.title(),
                        req.body(),
                        reviewPostRepository.findById(req.reviewPostId()).orElseThrow(),
                        memberService.findMember(testMemberId)
                )
        );
        CommunityPost post = communityService.findPost(createdPost);
        photoService.saveCommunityPhoto(req.files(),post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost (@PathVariable Long id, @RequestBody CommunityPostUpdateReq req) throws IOException {
        List<Long> deletedPhotoIds = req.deletedPhotoId();
        for (Long deletedPhotoId : deletedPhotoIds) {
            photoService.deletePhoto(deletedPhotoId);
        }
        List<MultipartFile> multipartFiles = req.newPhotos();
        CommunityPost post = communityService.findPost(id);
        photoService.saveCommunityPhoto(multipartFiles, post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public CommunityPostRes getPost(@PathVariable Long id){
        CommunityPost post = communityService.findPost(id);
        List<PhotoInPost> photoS3Paths = photoService.getPhotoS3Path(post);
        return new CommunityPostRes(
                post.getTitle(),
                post.getBody(),
                post.getCreateDate(),
                post.getLatsModifiedDate(),
                photoS3Paths,
                post.getQuotation().getId()
        );
    }

}
