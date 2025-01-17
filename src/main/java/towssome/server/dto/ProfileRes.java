package towssome.server.dto;

import towssome.server.entity.HashTag;

import java.util.List;

public record ProfileRes(
        String nickName,
        String profileImagePath,
        List<String> profileTagList,
        Long memberId,
        String username
) {
}
