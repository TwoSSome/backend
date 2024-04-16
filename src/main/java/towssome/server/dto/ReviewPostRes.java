package towssome.server.dto;

import towssome.server.entity.Member;

import java.util.List;

public record ReviewPostRes(
        String body,
        int price,
        Member member,
        List<String> path
) {
}
