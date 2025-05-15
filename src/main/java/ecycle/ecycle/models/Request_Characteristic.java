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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="assets")

public class Request_Characteristic {    

    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="max_price") private float max_price;
    @ManyToOne @JoinColumn(name="id_request") private Request request;
    @ManyToOne @JoinColumn(name="id_characteristics") private Characteristics characteristics;

}