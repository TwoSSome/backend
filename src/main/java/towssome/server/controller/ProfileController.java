package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;
import towssome.server.entity.Photo;
import towssome.server.service.BookMarkService;
import towssome.server.service.MemberService;
import towssome.server.service.PhotoService;
import towssome.server.service.ViewlikeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final PhotoService photoService;
    private final MemberAdvice memberAdvice;
    private final MemberService memberService;

    @GetMapping("/my")
    public ProfileRes getMyProfile(){
        Member jwtMember = memberAdvice.findJwtMember();

        List<HashTag> list = memberService.getProfileTag(jwtMember);
        ArrayList<HashtagRes> res = new ArrayList<>();
        for (HashTag hashTag : list) {
            res.add(new HashtagRes(
                    hashTag.getId(),
                    hashTag.getName()
            ));
        }

        return new ProfileRes(
                jwtMember.getNickName(),
                jwtMember.getProfilePhoto() == null ? null : jwtMember.getProfilePhoto().getS3Path(),
                res
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
        ArrayList<HashtagRes> res = new ArrayList<>();
        for (HashTag hashTag : list) {
            res.add(new HashtagRes(
                    hashTag.getId(),
                    hashTag.getName()
            ));
        }

        return new ProfileRes(
                member.getNickName(),
                member.getProfilePhoto().getS3Path(),
                res);
    }





}
