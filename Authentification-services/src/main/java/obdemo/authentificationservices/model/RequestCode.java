package obdemo.authentificationservices.model;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestCode {
    String email ;
    String code ;
}
