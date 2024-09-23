package towssome.server.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import towssome.server.entity.Member;
import towssome.server.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("일치하는 아이디가 없습니다"));

        if (member != null) {

            return new CustomUserDetails(member);
        }

        return null;
    }
}
