package com.example.rest.auth.services.authentication;

import com.example.rest.auth.dto.JwtAuthResponse;
import com.example.rest.auth.dto.UserSignInRequest;
import com.example.rest.auth.dto.UserSignUpRequest;
import com.example.rest.auth.exceptions.AuthSingInInvalid;
import com.example.rest.auth.exceptions.UserDiferentePasswords;
import com.example.rest.auth.repositories.AuthUsersRepository;
import com.example.rest.auth.services.jwt.JwtService;
import com.example.rest.users.exceptions.UserNameOrEmailExists;
import com.example.rest.users.models.Role;
import com.example.rest.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{
    private final AuthUsersRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationServiceImpl(AuthUsersRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    @Override
    public JwtAuthResponse signUp(UserSignUpRequest signUpRequestRequest) {
        log.info("signUp: {}", signUpRequestRequest);
        if (signUpRequestRequest.getPassword().contentEquals(signUpRequestRequest.getPasswordComprobacion())){
            User user = User.builder()
                    .nombre(signUpRequestRequest.getNombre())
                    .username(signUpRequestRequest.getUsername())
                    .password(passwordEncoder.encode(signUpRequestRequest.getPassword()))
                    .email(signUpRequestRequest.getEmail())
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();
            try {
                var usersaved = authRepository.save(user);
                return JwtAuthResponse.builder()
                        .token(jwtService.generateToken(usersaved))
                        .build();
            } catch (DataIntegrityViolationException e){
                throw new UserNameOrEmailExists("Username or email already exists");
            }
        }else {
            throw new UserDiferentePasswords("Passwords don't match");
        }
    }

    @Override
    public JwtAuthResponse signIn(UserSignInRequest signInRequest) {
        log.info("signIn: {}", signInRequest);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        var user = authRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new  AuthSingInInvalid("User or Password incorrect"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}