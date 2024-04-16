package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CommunityPostUpdateReq(
        String title,
        String body,
        List<Long> deletedPhotoId,
        List<MultipartFile> newPhotos
) {
}
