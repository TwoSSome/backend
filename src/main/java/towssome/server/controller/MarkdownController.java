package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.TempImageNameRes;
import towssome.server.service.MarkdownService;

@RestController
@RequiredArgsConstructor
public class MarkdownController {

    private final MarkdownService markdownService;

    @PostMapping("/markdown/image")
    public ResponseEntity<?> saveTempImage(MultipartFile file){

        String tempImageName = markdownService.saveAndGetTempImage(file);

        return new ResponseEntity<>(new TempImageNameRes(tempImageName), HttpStatus.OK);
    }

}
