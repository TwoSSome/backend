package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.service.MatingService;

@RestController
@RequiredArgsConstructor
public class MatingController {

    private final MatingService matingService;

}
