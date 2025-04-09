package obdemo.authentificationservices.repsitory;

import obdemo.authentificationservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Vérifier si un utilisateur existe par email
    boolean existsByEmail(String email);

    // Vérifier si un utilisateur existe par cin
   // boolean existsByCin(String cin);
}
