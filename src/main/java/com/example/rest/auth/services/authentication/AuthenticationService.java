package com.example.rest.auth.services.authentication;


import com.example.rest.auth.dto.JwtAuthResponse;
import com.example.rest.auth.dto.UserSignInRequest;
import com.example.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}