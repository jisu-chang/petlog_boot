package com.example.PetLog.User;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userLoginId) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        UserEntity userEntity = userRepository.findByUserLoginId(userLoginId) ;

        if (userEntity != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
            return new User(userEntity.getUserLoginId(),userEntity.getPassword(), grantedAuthorities);
        }

        else {
            throw new UsernameNotFoundException("can not find User : " + userLoginId);
        }
    }
}