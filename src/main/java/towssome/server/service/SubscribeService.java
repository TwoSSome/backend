package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.controller.PageResult;
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
    public PageResult<SubscribeRes> getSubscribePage(Member subscriber, int page, int size) {
        ArrayList<SubscribeRes> subscribeRes = new ArrayList<>();
        Page<Subscribe> subscribePage = subscribeRepository.findAllBySubscriber(subscriber,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")));
        for (Subscribe subscribe : subscribePage.getContent()) {

            String profilePhotoPath =
                    subscribe.getFollowed().getProfilePhoto() == null ? null :
                            subscribe.getFollowed().getProfilePhoto().getS3Path();

            subscribeRes.add(new SubscribeRes(
                    profilePhotoPath,
                    subscribe.getFollowed().getNickName(),
                    subscribe.getId(),
                    subscribe.getFollowed().getId(),
                    subscribe.getCreateDate()
            ));
        }

        return new PageResult<>(
                subscribeRes,
                subscribePage.getTotalElements(),
                subscribePage.getTotalPages(),
                page,
                size
        );
    }

    public CursorResult<>

    public Subscribe getSubscribe(Long id) {
        return subscribeRepository.findById(id).orElseThrow();
    }

    private List<Subscribe> getAllSubscribe(Member member) {
        return subscribeRepository.findAllBySubscriber(member);
    }




}
