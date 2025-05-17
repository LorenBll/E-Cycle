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
    @Column(name="username",length=50) private String username;
    @Column(name="name",length=50) private String name;
    @Column(name="surname",length=50) private String surname;
    @Column(name="email",length=100) private String email;
    @Column(name="password",length=64) private String password;
    // address components
    @Column(name="state",length=50) private String state;
    @Column(name="region",length=50) private String region;
    @Column(name="province",length=50) private String province;
    @Column(name="city",length=50) private String city;
    @Column(name="street",length=100) private String street;
    @Column(name="civic",length=10) private String civic;
}