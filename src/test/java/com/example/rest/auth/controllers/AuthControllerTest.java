package com.example.rest.auth.controllers;

import com.example.rest.auth.dto.JwtAuthResponse;
import com.example.rest.auth.dto.UserSignInRequest;
import com.example.rest.auth.dto.UserSignUpRequest;
import com.example.rest.auth.exceptions.AuthSingInInvalid;
import com.example.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import com.example.rest.auth.exceptions.UserDiferentePasswords;
import com.example.rest.auth.services.authentication.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private final String myEndpoint = "/v1/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthenticationService authService;

    @Autowired
    public AuthControllerTest(AuthenticationService authService) {
        this.authService = authService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void signUp() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Alina", "Alina Test", "alina", "alina@test.com", "alina", "alina");
        var jwtAuthResponse = new JwtAuthResponse("tokenTest");
        var myLocalEndpoint = myEndpoint + "/signup";

        when(authService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpDiferentsPasswords() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("alina");
        request.setPassword("123");
        request.setPasswordComprobacion("1234");
        request.setEmail("alina@test.com");
        request.setNombre("alina");
        request.setApellidos("alina l");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentePasswords("Las contraseñas no coinciden"));

        assertThrows(UserDiferentePasswords.class, () -> authService.signUp(request));

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpFalseUsernameOrEmailExist() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("alina1");
        request.setPassword("123");
        request.setPasswordComprobacion("1234");
        request.setEmail("alina1@test.com");
        request.setNombre("alina");
        request.setApellidos("alina l");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe"));

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authService.signUp(request));

        // Verify
        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }


    @Test
    void signUpFalseEmptyFields() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";

        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("123");
        request.setPasswordComprobacion("123");
        request.setEmail("");
        request.setNombre("");
        request.setApellidos("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Apellidos no puede ")),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
        );
    }
    @Test
    void signInFalseIncorrectUsernameOrPassword() {
        var myLocalEndpoint = myEndpoint + "/signin";

        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("hola");
        request.setPassword("hola");

        when(authService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        assertThrows(AuthSingInInvalid.class, () -> authService.signIn(request));

        verify(authService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signInFalseEmptyUsernameAndPassword() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
                //() -> assertTrue(response.getContentAsString().contains("Password no puede"))
        );
    }
}