package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.service.PhotoService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    private final PhotoService photoService;

    @PostMapping("/upload")
    public String upload(@RequestBody MultipartFile file){

        String url = null;


        return url;
    }

}
