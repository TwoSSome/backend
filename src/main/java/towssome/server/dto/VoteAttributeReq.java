package towssome.server.dto;

import org.springframework.web.multipart.MultipartFile;

public record VoteAttributeReq(
        String title,
        MultipartFile file
) {
}
