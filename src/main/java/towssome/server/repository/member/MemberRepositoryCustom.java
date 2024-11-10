package towssome.server.repository.member;

import towssome.server.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findRanker(int size);

    void reconfigPassword(String password, String email);

}
