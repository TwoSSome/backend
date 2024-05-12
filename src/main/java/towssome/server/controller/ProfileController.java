package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.ProfileRes;
import towssome.server.dto.UpdateProfileReq;
import towssome.server.entity.Member;
import towssome.server.entity.Photo;
import towssome.server.service.BookMarkService;
import towssome.server.service.MemberService;
import towssome.server.service.PhotoService;
import towssome.server.service.ViewlikeService;

import java.io.IOException;

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

        return new ProfileRes(
                jwtMember.getNickName(),
                jwtMember.getProfilePhoto() == null ? null : jwtMember.getProfilePhoto().getS3Path(),
                null
        );
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

        return null;
    }





}
