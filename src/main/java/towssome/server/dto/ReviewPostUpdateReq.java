package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ReviewPostUpdateReq(
        String body,
        int price,
        Long memberId,
        List<Long> willDeletePhoto,
        List<MultipartFile> willAddPhoto
) {
}
