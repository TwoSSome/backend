package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import towssome.server.repository.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarPersonalScheduleRepository calendarPersonalScheduleRepository;
    private final CalendarScheduleRepository calendarScheduleRepository;
    private final CalendarTagRepository calendarTagRepository;
    private final CalendarPostCommentRepository calendarPostCommentRepository;
    private final CalendarPostRepository calendarPostRepository;




}
