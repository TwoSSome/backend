package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.CursorResult;
import towssome.server.dto.HashtagRes;
import towssome.server.dto.SubscribeDTO;
import towssome.server.dto.SubscribeRes;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.entity.Subscribe;
import towssome.server.repository.ProfileTagRepository;
import towssome.server.repository.SubscribeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final ProfileTagRepository profileTagRepository;

    public Subscribe addSubscribe(Member subscriber, Member following) {
        Subscribe subscribe = new Subscribe(
                subscriber,
                following
        );
        return subscribeRepository.save(subscribe);
    }

    public void cancelSubscribe(Subscribe subscribe) {
        subscribeRepository.delete(subscribe);
    }

    /**
     * 구독 리스트의 id와 구독일자를 찾기
     * @param member
     * @return
     */
    public List<SubscribeDTO> getSubscribes(Member member) {
        ArrayList<SubscribeDTO> dtoList = new ArrayList<>();
        List<Subscribe> allSubscribe = getAllSubscribe(member);

        for (Subscribe subscribe : allSubscribe) {
            dtoList.add(new SubscribeDTO(
                    subscribe.getId(),
                    subscribe.getCreateDate()
            ));
        }

        return dtoList;
    }

    @Transactional
    public CursorResult<SubscribeRes> getSubscribePage(Member subscriber, int page, int size) {
        ArrayList<SubscribeRes> subscribeRes = new ArrayList<>();
        Slice<Subscribe> subscribePage = subscribeRepository.findAllBySubscriber(subscriber,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")));
        for (Subscribe subscribe : subscribePage.getContent()) {

            String profilePhotoPath =
                    subscribe.getFollowed().getProfilePhoto() == null ? null :
                            subscribe.getFollowed().getProfilePhoto().getS3Path();

            List<ProfileTag> list = profileTagRepository.findAllByMember(subscribe.getFollowed());
            ArrayList<HashtagRes> hashtags = new ArrayList<>();
            for (ProfileTag profileTag : list) {
                hashtags.add(new HashtagRes(
                        profileTag.getHashTag().getId(),
                        profileTag.getHashTag().getName())
                );
            }

            subscribeRes.add(new SubscribeRes(
                    profilePhotoPath,
                    subscribe.getFollowed().getNickName(),
                    subscribe.getId(),
                    subscribe.getFollowed().getId(),
                    subscribe.getCreateDate(),
                    hashtags
            ));
        }

        return new CursorResult<>(
                subscribeRes,
                (long)page+2,
                subscribePage.hasNext()
        );
    }

    public Subscribe getSubscribe(Long id) {
        return subscribeRepository.findById(id).orElseThrow();
    }

    private List<Subscribe> getAllSubscribe(Member member) {
        return subscribeRepository.findAllBySubscriber(member);
    }




}
