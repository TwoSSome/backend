package towssome.server.controller;

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
import towssome.server.service.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final MemberAdvice memberAdvice;
    private final MemberService memberService;
    private final ReviewPostService reviewPostService;
    private static final int PAGE_SIZE = 5;


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

    @PostMapping("/createVirtual")
    public ResponseEntity<?> createVirtual(@RequestBody CreateVirtualRes res){

        Member jwtMember = memberAdvice.findJwtMember();
        memberService.createVirtual(res.hashtagList(),jwtMember);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<?> updateProfileImage(@RequestBody MultipartFile photo) {
        Member jwtMember = memberAdvice.findJwtMember();
        memberService.changeProfilePhoto(jwtMember, photo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileReq req){
        Member jwtMember = memberAdvice.findJwtMember();
        memberService.changeProfile(jwtMember, req.nickName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

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

    @GetMapping("/review")
    public CursorResult<ReviewSimpleRes> getMyReviews(@RequestParam(value = "cursorId", required = false) Long cursorId,
                                                    @RequestParam(value = "size", required = false) Integer size,
                                                    @RequestParam(value = "sort", required = false) String sort) {
        Member member = memberAdvice.findJwtMember();
        if(size == null) size = PAGE_SIZE;
        return reviewPostService.getMyReviewPage(member, cursorId, sort, PageRequest.of(0, size));
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
