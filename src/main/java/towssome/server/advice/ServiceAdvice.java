package towssome.server.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import towssome.server.entity.*;
import towssome.server.repository.CalendarTagRepository;
import towssome.server.repository.hashtag.HashTagRepository;
import towssome.server.repository.ProfileTagRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceAdvice {

    private final HashTagRepository hashTagRepository;
    private final ProfileTagRepository profileTagRepository;
    private final CalendarTagRepository calendarTagRepository;

    public void storeHashtag(List<String> list, Member member) {
        ArrayList<HashTag> hashTags = new ArrayList<>();

        for (String name : list) {
            if (hashTagRepository.existsByName(name)) {
                hashTags.add(hashTagRepository.findByName(name));
            }else{
                HashTag save = hashTagRepository.save(new HashTag(
                        name,
                        0L
                ));
                hashTags.add(save);
            }
        }

        for (HashTag hashTag : hashTags) {
            profileTagRepository.save(new ProfileTag(
                    member,
                    hashTag
            ));
        }
    }

    //캘린더 생성 시 기본 태그 6개 생성
    //n주년, 생일, OO데이, 데이트, 남친/여친 개인 일정
    public void calendarInitialize(Calendar calendar) {
        CalendarTag OOthAnniversary = new CalendarTag(
                "n주년",
                1,
                calendar,
                true,
                false
        );
        CalendarTag birthday = new CalendarTag(
                "생일",
                2,
                calendar,
                true,
                false
        );
        CalendarTag couplesHoliday = new CalendarTag(
                "연인 기념일",
                3,
                calendar,true,
                false
        );
        CalendarTag date = new CalendarTag(
                "데이트",
                4,
                calendar,true,
                false
        );
        CalendarTag mans = new CalendarTag(
                "남자친구 일정",
                4,
                calendar,true,
                true
        );
        CalendarTag girls = new CalendarTag(
                "데이트",
                4,
                calendar,true,
                true
        );
        calendarTagRepository.save(OOthAnniversary);
        calendarTagRepository.save(birthday);
        calendarTagRepository.save(couplesHoliday);
        calendarTagRepository.save(date);
        calendarTagRepository.save(mans);
        calendarTagRepository.save(girls);

    }


}
