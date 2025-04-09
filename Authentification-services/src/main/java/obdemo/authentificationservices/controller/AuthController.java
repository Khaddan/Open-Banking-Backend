package obdemo.authentificationservices.controller;

import lombok.RequiredArgsConstructor;
import obdemo.authentificationservices.dto.AuthRequest;
import obdemo.authentificationservices.dto.AuthResponse;
import obdemo.authentificationservices.dto.RegisterRequest;
import obdemo.authentificationservices.model.RequestCode;
import obdemo.authentificationservices.model.RequestPassword;
import obdemo.authentificationservices.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) throws NoSuchAlgorithmException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) {
        return authService.sendResetCode(email);
    }

    @PostMapping("/verify-reset-code")
    public boolean verifyResetCode(@RequestBody RequestCode request) {
        return authService.verifyResetCode(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody RequestPassword requestPassword){
        return authService.resetPassword(requestPassword);
    }


}

