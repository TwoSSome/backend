package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.service.BookMarkService;
import towssome.server.service.ViewlikeService;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    //리뷰글 조회/좋아요가 먼저 선행되어야 함

    private final ViewlikeService viewlikeService;
    private final BookMarkService bookMarkService;




}
