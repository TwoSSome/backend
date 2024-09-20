package towssome.server.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.repository.HashTagRepository;
import towssome.server.repository.ProfileTagRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceAdvice {

    private final HashTagRepository hashTagRepository;
    private final ProfileTagRepository profileTagRepository;


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



}
