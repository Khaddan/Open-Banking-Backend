package obdemo.authentificationservices.model;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestPassword {
    String email ;
    String password ;
}
