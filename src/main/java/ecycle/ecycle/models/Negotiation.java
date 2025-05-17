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
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="negotiations")
public class Negotiation { 
    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="ts_creation") private Timestamp tsCreation;
    @Column(name="ts_closure") private Timestamp tsClosure;
    @Column(name="wasAccepted") private boolean wasAccepted;
    @ManyToOne @JoinColumn(name="id_sing_offer") private SingOffer singOffer;
    @ManyToOne @JoinColumn(name="id_sing_request") private SingRequest singRequest;
}