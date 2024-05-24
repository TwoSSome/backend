package towssome.server.dto;

import towssome.server.entity.Photo;

import java.util.List;

public record ReviewSimpleRes(
        Photo profilePhoto,
        String nickname,
        String bodyPhoto,
        List<String> hashTags
) {
}
