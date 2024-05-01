package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ReviewPostReq(
        String body,
        int price,
        Long memberId,
        List<MultipartFile> photos
) {
}
