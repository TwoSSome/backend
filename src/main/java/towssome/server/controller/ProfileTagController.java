package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.dto.AllMemberProfileTagRes;
import towssome.server.service.ProfileTagService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileTagController {

    private final ProfileTagService profileTagService;

    @GetMapping("/allmemberprofiletag")
    public ResponseEntity<Map<Long, AllMemberProfileTagRes>> getAllMembersWithTags() {
        Map<Long, AllMemberProfileTagRes> memberTagsMap = profileTagService.getAllMembersWithTags();

        return ResponseEntity.ok(memberTagsMap);
    }
}