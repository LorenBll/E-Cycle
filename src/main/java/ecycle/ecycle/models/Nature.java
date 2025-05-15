package ecycle.ecycle.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="natures")

public class Nature {    

    @Id @Column(name="ID", length=50) private String id;
    
}