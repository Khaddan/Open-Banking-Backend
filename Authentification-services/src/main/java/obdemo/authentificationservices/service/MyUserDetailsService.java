package obdemo.authentificationservices.service;

import lombok.RequiredArgsConstructor;
import obdemo.authentificationservices.entity.User;
import obdemo.authentificationservices.repsitory.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    // Méthode qui charge un utilisateur par email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Récupérer l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec cet email"));

        // Construire l'objet UserDetails avec les informations de l'utilisateur
        org.springframework.security.core.userdetails.User.UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        userBuilder.password(user.getPassword());
        userBuilder.roles("USER");  // Vous pouvez ajouter des rôles ici, par exemple "USER", "ADMIN", etc.

        return userBuilder.build();
    }
}
