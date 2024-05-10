package towssome.server.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import towssome.server.entity.Category;
import towssome.server.entity.Member;
import towssome.server.exception.DuplicateIdException;
import towssome.server.repository.CategoryRepository;
import towssome.server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CategoryRepository categoryRepository;

    public Member joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.username();
        String password = joinDTO.password();
        String nickname = joinDTO.nickname();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateIdException("중복된 아이디입니다");
        }

        Member member = new Member(
                username,
                bCryptPasswordEncoder.encode(password),
                nickname,
                0,
                null, // 프로필 사진 설정을 가능케 할 것인가?
                "ROLE_USER"
        );

        userRepository.save(member);

        //마스터 카테고리 생성
        Category category = new Category(
                "북마크",
                member
        );
        categoryRepository.save(category);

        return member;
    }


}
