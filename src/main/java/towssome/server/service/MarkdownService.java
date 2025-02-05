package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkdownService {

    // 윈도우 환경에서 사용할 저장 경로
    private static final String TEMP_IMAGE_DIR = "src\\main\\java\\towssome\\server\\temp_image\\";

    public String saveAndGetTempImage(MultipartFile file) {
        // 원본 파일 이름 가져오기
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 없습니다.");
        }

        // 확장자 추출
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 유니크한 파일 이름 생성
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 저장할 디렉토리 생성 (없으면 생성)
        File directory = new File(TEMP_IMAGE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장 경로 설정
        Path filePath = Path.of(TEMP_IMAGE_DIR, uniqueFilename);

        try {
            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }

        // 저장된 파일 이름 반환
        return uniqueFilename;
    }


}
