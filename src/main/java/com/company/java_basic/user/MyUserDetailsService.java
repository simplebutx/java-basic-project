package com.company.java_basic.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

//로그인 시 DB에서 유저 정보를 조회해서 👉 Spring Security가 이해할 수 있는 형태로 전달해주는 역할
@Service
// 👉 이 클래스가 "비즈니스 로직을 담당하는 서비스"임을 Spring에 알림
// 👉 Spring Security가 로그인 시 자동으로 이 클래스를 사용하게 됨
@RequiredArgsConstructor
// 👉 final 이 붙은 필드(userRepository)를 생성자를 통해 자동 주입(DI)해주는 Lombok 어노테이션
public class MyUserDetailsService implements UserDetailsService {
    // 👉 Spring Security가 "로그인 시 유저 정보를 가져올 때" 반드시 호출하는 인터페이스
    private final UserRepository userRepository;
    // DB에서 유저 정보를 조회하기 위한 Repository
    // 👉 username으로 회원을 찾기 위해 사용됨

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Spring Security가 로그인 요청 시 자동으로 호출하는 메서드
        // 👉 로그인 폼에서 입력한 username이 여기로 전달됨
        // 👉 이 메서드의 목적:"이 username에 해당하는 유저가 DB에 있는지 확인하고 있다면 인증에 필요한 정보(UserDetails)를 반환"
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("그런아이디없음"));
        // DB에서 username으로 유저 조회 👉 Optional<User> 반환
        // orElseThrow: 👉 유저가 없으면 로그인 실패 처리
        // 👉 Spring Security가 이 예외를 캐치해서 "아이디 또는 비밀번호가 잘못되었습니다" 같은 에러로 처리함
        List<GrantedAuthority> authorities;

        if (user.getUsername().equals("admin")) {
            authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        // 이 유저가 가진 권한 목록 생성
        // 👉 ROLE_USER 는 "일반 사용자 권한"

        // Spring Security 규칙: 👉 권한 이름은 반드시 "ROLE_"로 시작해야 함
        // 👉 나중에 .hasRole("USER") 같은 접근 제어에 사용됨
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
                // Spring Security가 이해할 수 있는 User 객체로 변환해서 반환

                // ✔ username  : 로그인 아이디
                // ✔ password  : DB에 저장된 암호화된 비밀번호 (BCrypt)
                // ✔ authorities: 이 유저의 권한 목록

                // 👉 이 객체를 기준으로 Spring Security가
                //    1️⃣ 비밀번호 비교
                //    2️⃣ 권한 체크
                //    3️⃣ 로그인 성공 / 실패 판단
        );
    }
}
