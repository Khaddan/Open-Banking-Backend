package obdemo.authentificationservices.service;

import lombok.RequiredArgsConstructor;
import obdemo.authentificationservices.dto.AuthRequest;
import obdemo.authentificationservices.dto.AuthResponse;
import obdemo.authentificationservices.dto.RegisterRequest;
import obdemo.authentificationservices.entity.User;
import obdemo.authentificationservices.repsitory.UserRepository;
import obdemo.authentificationservices.securiter.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .nom(request.getNom())
                .password(encoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        mailService.sendEmail(user.getEmail(), "Bienvenue !", "Votre compte a été créé.");

        return new AuthResponse(jwtUtil.generateToken((UserDetails) user));
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new AuthResponse(jwtUtil.generateToken((UserDetails) user));
    }
}

