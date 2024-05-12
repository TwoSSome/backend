package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.Subscribe;
import towssome.server.repository.SubscribeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

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
    public CursorResult<SubscribeRes> getSubscribeSlice(Member member, int offset, int limit) {
        ArrayList<SubscribeRes> subscribeRes = new ArrayList<>();
        SubscribeSliceDTO subscribeSliceDTO = subscribeRepository.subscribeSlice(member, offset, limit);
        for (Subscribe subscribe : subscribeSliceDTO.subscribes()) {
            subscribeRes.add(new SubscribeRes(
                    subscribe.getFollowed().getProfilePhoto().getS3Path(),
                    subscribe.getFollowed().getNickName(),
                    subscribe.getId(),
                    subscribe.getFollowed().getId(),
                    subscribe.getCreateDate()
            ));
        }

        return new CursorResult<>(
                subscribeRes,
                subscribeSliceDTO.hasNext()
        );
    }

    public Subscribe getSubscribe(Long id) {
        return subscribeRepository.findById(id).orElseThrow();
    }

    private List<Subscribe> getAllSubscribe(Member member) {
        return subscribeRepository.findAllBySubscriber(member);
    }




}
