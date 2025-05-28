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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="interactions")
public class Interaction { 
    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="title",length=50) private String title;
    @Column(name="ts_creation") private Timestamp tsCreation;
    @Column(name="isOffer") private boolean isOffer;
    @ManyToOne @JoinColumn(name="id_user") private User user;

    public void setIsOffer(boolean isOffer) {
        this.isOffer = isOffer;
    }
    public boolean getIsOffer() {
        return isOffer;
    }

}