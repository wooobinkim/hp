package com.ssafy.hp.security.service;

import com.ssafy.hp.NotFoundException;
import com.ssafy.hp.security.oauth.*;
import com.ssafy.hp.user.UserRepository;
import com.ssafy.hp.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ssafy.hp.NotFoundException.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User findUser = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(findUser.getRole())));

        LoginUserDetails loginUserDetails = new LoginUserDetails(String.valueOf(findUser.getUserId()), "", authorities);
        loginUserDetails.setUser(findUser);
        return loginUserDetails;
    }
}