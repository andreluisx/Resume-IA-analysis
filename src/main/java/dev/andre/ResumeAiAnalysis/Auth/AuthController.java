package dev.andre.ResumeAiAnalysis.Auth;


import dev.andre.ResumeAiAnalysis.Auth.Exceptions.EmailAlreadyExist;
import dev.andre.ResumeAiAnalysis.Config.TokenService;
import dev.andre.ResumeAiAnalysis.User.*;
import dev.andre.ResumeAiAnalysis.User.Exceptions.EmailOrPasswordInvalid;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.reflect.Type;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserEntity user = userService.RegisterUser(UserMapper.toUser(request));
        return ResponseEntity.ok().body(UserMapper.toUserResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) throws BadRequestException {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok().body(new LoginResponse(token));


    }

}
