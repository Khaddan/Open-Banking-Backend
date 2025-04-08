package obdemo.authentificationservices.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import obdemo.authentificationservices.dto.AuthRequest;
import obdemo.authentificationservices.dto.AuthResponse;
import obdemo.authentificationservices.dto.RegisterRequest;
import obdemo.authentificationservices.entity.User;
import obdemo.authentificationservices.model.RequestCode;
import obdemo.authentificationservices.model.RequestPassword;
import obdemo.authentificationservices.repsitory.UserRepository;
import obdemo.authentificationservices.securiter.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    public AuthResponse register(RegisterRequest request) throws NoSuchAlgorithmException {
        User user = User.builder()
                .email(request.getEmail())
                .nom(request.getNom())
                .password(hashPassword(encoder.encode(request.getPassword())))
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


    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> resetCodes = new HashMap<>();

    public ResponseEntity<?> sendResetCode(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            String resetCode = UUID.randomUUID().toString().substring(0, 6); // Generate a 6-character code
            resetCodes.put(email, resetCode);

            // Send reset code to user's email
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setTo(email);
                helper.setSubject("Password Reset Code");

                // HTML message with the reset code in bold
                String htmlMsg = "<p>Hello,</p>"
                        + "<p>You requested to reset your password. Here is your reset code:</p>"
                        + "<h3><strong>" + resetCode + "</strong></h3>"  // Bold code
                        + "<p>Please use this code to reset your password.</p>"
                        + "<p>If you didn't request a password reset, you can ignore this email.</p>"
                        + "<p>Best regards,<br>Open Banking Team</p>";

                helper.setText(htmlMsg, true); // true indicates HTML content

                mailSender.send(mimeMessage);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("{\"success\": false, \"message\": \"Failed to send email\"}");
            }

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Reset code sent to your email\"}");
        } else {
            return ResponseEntity.status(404).body("{\"success\": false, \"message\": \"User not found\"}");
        }
    }


    public boolean verifyResetCode(RequestCode request) {
        String email = request.getEmail();
        String code = request.getCode();
        if (resetCodes.containsKey(email) && resetCodes.get(email).equals(code)) {
            return true;
        } else {
            return false;
        }
    }



    public ResponseEntity<?> resetPassword(RequestPassword request) {
        String email = request.getEmail();
        String newPassword = request.getPassword();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            try {
                // Hacher le mot de passe avec gestion de l'exception
                String hashedPassword = hashPassword(newPassword);
                user.setPassword(hashedPassword);
                userRepository.save(user);
                return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Password updated\"}");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("{\"success\": false, \"message\": \"Error hashing password\"}");
            }
        } else {
            return ResponseEntity.status(404).body("{\"success\": false, \"message\": \"User not found\"}");
        }

    }



    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Créez une instance de SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Hachez le mot de passe
        byte[] hashedBytes = md.digest(password.getBytes());

        // Convertissez les octets en une chaîne hexadécimale
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }




}

