package dev.andre.ResumeAiAnalysis.Auth;

import dev.andre.ResumeAiAnalysis.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(user).orElseThrow(() -> new UsernameNotFoundException("Usuario ou senha inválido."));
    }
}
