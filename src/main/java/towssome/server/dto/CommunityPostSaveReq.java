package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;
import towssome.server.entity.ReviewPost;

import java.util.List;

public record CommunityPostSaveReq(
        String title,
        String body,
        Long reviewPostId,
        List<MultipartFile> files
) {
}
