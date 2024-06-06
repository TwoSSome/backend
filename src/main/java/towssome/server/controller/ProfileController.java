package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;
import towssome.server.service.MemberService;
import towssome.server.service.ReviewPostService;
import towssome.server.service.ViewlikeService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "프로필", description = "프로필 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final MemberAdvice memberAdvice;
    private final MemberService memberService;
    private final ReviewPostService reviewPostService;
    private final ViewlikeService viewlikeService;
    private static final int PAGE_SIZE = 5;


    @Operation(summary = "내 프로필 조회 API",description = "닉네임, 프로필 사진, id 반환")
    @GetMapping("/my")
    public ProfileRes getMyProfile(){
        Member jwtMember = memberAdvice.findJwtMember();

        List<HashTag> list = memberService.getProfileTag(jwtMember);
        ArrayList<String > res = new ArrayList<>();
        for (HashTag hashTag : list) {
            res.add(hashTag.getName());
        }

        return new ProfileRes(
                jwtMember.getNickName(),
                jwtMember.getProfilePhoto() == null ? null : jwtMember.getProfilePhoto().getS3Path(),
                res,
                jwtMember.getId()
        );
    }

    @Operation(summary = "가상 연인 프로필 생성 API", description = "해시태그 리스트를 받아서 생성")
    @PostMapping("/createVirtual")
    public ResponseEntity<?> createVirtual(@RequestBody CreateVirtualRes res){

        Member jwtMember = memberAdvice.findJwtMember();
        memberService.createVirtual(res.hashtagList(),jwtMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    



    @Operation(summary = "프로필 사진 변경 API", description = "이전 사진이 있을 경우 삭제되고 교체됨",
    parameters = @Parameter(name = "photo", description = "multipart/form-data 형식으로 보내야 함"))
    @PostMapping("/updateProfileImage")
    public ResponseEntity<?> updateProfileImage(@RequestBody MultipartFile photo) {
        Member jwtMember = memberAdvice.findJwtMember();
        memberService.changeProfilePhoto(jwtMember, photo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "프로필 변경 API", description = "현재 닉네임만 교체 가능")
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileReq req){
        Member jwtMember = memberAdvice.findJwtMember();
        memberService.changeProfile(jwtMember, req.nickName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "프로필 조회 API", description = "멤버 id로 조회 가능")
    @GetMapping("/{id}")
    public ProfileRes getProfile(@PathVariable Long id){

        Member member = memberService.getMember(id);
        List<HashTag> list = memberService.getProfileTag(member);
        ArrayList<String > res = new ArrayList<>();
        for (HashTag hashTag : list) {
            res.add(hashTag.getName());
        }

        return new ProfileRes(
                member.getNickName(),
                member.getProfilePhoto() == null ? null : member.getProfilePhoto().getS3Path(),
                res,
                member.getId());
    }

    @Operation(summary = "타인 리뷰글 조회 API",
            description = "cursorId를 기준으로 리뷰글 반환, 예를 들어 cursorId가 10이면 id가 10이하인 리뷰글을 size만큼 가져옴")
    @GetMapping("/{id}/review")
    public CursorResult<ReviewSimpleRes> getOtherReviews(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                         @RequestParam(value = "size", required = false) Integer size,
                                                         @RequestParam(value = "sort", required = false) String sort,
                                                         @PathVariable String id) {
        Member member = memberService.getMember(id);
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getMyReviewPage(member, cursorId, sort, PageRequest.of(0, size));
    }

    @Operation(summary = "내 리뷰글 조회 API",
            description = "cursorId를 기준으로 리뷰글 반환, 예를 들어 cursorId가 10이면 id가 10이하인 리뷰글을 size만큼 가져옴")
    @GetMapping("/review")
    public CursorResult<ReviewSimpleRes> getMyReviews(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                    @RequestParam(value = "size", required = false) Integer size,
                                                    @RequestParam(value = "sort", required = false) String sort) {
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getMyReviewPage(member, cursorId, sort, PageRequest.of(0, size));
    }

    @Operation(summary = "내 조회글 조회 API", description = "cursorId를 기준으로 리뷰글 반환, 예를 들어 cursorId가 10이면 id가 10이하인 리뷰글을 size만큼 가져옴")
    @GetMapping("/view")
    public CursorResult<ReviewSimpleRes> getRecentView(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                     @RequestParam(value = "size", required = false) Integer size,
                                                     @RequestParam(value = "sort", required = false) String sort) { // get all review(size 만큼의 리뷰글과 다음 리뷰글의 존재여부(boolean) 전달)
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getRecentView(member, cursorId, sort, PageRequest.of(0, size));
    }

    @Operation(summary = "내가 좋아요한 글 조회 API", description = "cursorId를 기준으로 리뷰글 반환, 예를 들어 cursorId가 10이면 id가 10이하인 리뷰글을 size만큼 가져옴")
    @GetMapping("/like")
    public CursorResult<ReviewSimpleRes> getLike(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                               @RequestParam(value = "size", required = false) Integer size,
                                               @RequestParam(value = "sort", required = false) String sort) {
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return viewlikeService.getLike(member, cursorId, sort, PageRequest.of(0, size));
    }

    @GetMapping("/search")
    public ProfileRes searchProfile(@RequestParam(value="username") String username) {
        Member searchedMember = memberService.getMember(username);
        List<HashTag> list = memberService.getProfileTag(searchedMember);

        ArrayList<String> res = new ArrayList<>();
        for (HashTag hashTag : list) {
            res.add(hashTag.getName());
        }

        return new ProfileRes(
                searchedMember.getNickName(),
                searchedMember.getProfilePhoto() == null ? null : searchedMember.getProfilePhoto().getS3Path(),
                res,
                searchedMember.getId()
        );
    }


}
