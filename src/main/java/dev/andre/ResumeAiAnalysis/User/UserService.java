package dev.andre.ResumeAiAnalysis.User;

import dev.andre.ResumeAiAnalysis.Auth.Exceptions.EmailAlreadyExist;
import dev.andre.ResumeAiAnalysis.Config.JWTUserData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity RegisterUser(UserEntity user) {
        Optional<UserEntity> userByEmail = userRepository.findUserByEmail(user.getEmail());

        if(userByEmail.isPresent()) {
            throw new EmailAlreadyExist("Este email já está sendo usado");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<UserEntity> getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        String email = (principal instanceof JWTUserData jwtUserData)
                ? jwtUserData.email()
                : null;

        return getUserByEmail(email);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

}
