package towssome.server.repository;

import towssome.server.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findRanker(int size);


}
