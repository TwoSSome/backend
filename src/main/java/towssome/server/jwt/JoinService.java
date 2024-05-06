package towssome.server.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import towssome.server.entity.Member;
import towssome.server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.username();
        String password = joinDTO.password();
        String nickname = joinDTO.nickname();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        Member userEntity = new Member(
                username,
                bCryptPasswordEncoder.encode(password),
                nickname,
                0,
                null,
                "ROLE_USER"
        );

        userRepository.save(userEntity);

    }


}
