package com.example.rest.auth.services;

import com.example.rest.auth.dto.JwtAuthResponse;
import com.example.rest.auth.dto.UserSignInRequest;
import com.example.rest.auth.dto.UserSignUpRequest;
import com.example.rest.auth.exceptions.AuthSingInInvalid;
import com.example.rest.auth.exceptions.UserDiferentePasswords;
import com.example.rest.auth.repositories.AuthUsersRepository;
import com.example.rest.auth.services.authentication.AuthenticationServiceImpl;
import com.example.rest.auth.services.jwt.JwtService;
import com.example.rest.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthUsersRepository authUsersRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void signUp(){
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("user@test.com");
        request.setNombre("UserTest");
        request.setApellidos("User Testeado");

        User userStored = new User();
        String token = "tokenTest";

        when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

        when(jwtService.generateToken(userStored)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signUp(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    void signUpDifferentPassword()  {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setPasswordComprobacion("passwordTest");
        request.setEmail("user@test.com");
        request.setNombre("UserTest");
        request.setApellidos("User Testeado");

        assertThrows(UserDiferentePasswords.class, () -> authenticationService.signUp(request));
    }


    @Test
    void signIn(){
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("alina");
        request.setPassword("123");

        User user = new User();
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        String token = "tokenTest";
        when(jwtService.generateToken(user)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signIn(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    void signInFalseInvalidUsernameOrPassword(){
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("Alina");
        request.setPassword("123");

        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(AuthSingInInvalid.class, () -> authenticationService.signIn(request));
    }
}
