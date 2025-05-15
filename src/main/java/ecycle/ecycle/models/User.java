package ecycle.ecycle.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")

public class User {    

    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="username") private String username;
    @Column(name="name") private String name;
    @Column(name="surname") private String surname;
    @Column(name="email") private String email;
    @Column(name="password") private String password;
    // address components
    @Column(name="state") private String state;
    @Column(name="region") private String region;
    @Column(name="province") private String province;
    @Column(name="city") private String city;
    @Column(name="street") private String street;
    @Column(name="civic") private String civic;

}