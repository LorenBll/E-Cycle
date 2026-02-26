package ecycle.ecycle.models.bodies;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String state;
    private String region;
    private String province;
    private String city;
    private String street;
    private String civic;
}

