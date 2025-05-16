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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sing_requests")

public class SingRequest {
    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="max_price") private float max_price;
    @Column(name="ts_deletion") private Timestamp tsDeletion;
    @ManyToOne @JoinColumn(name="id_request") private Interaction request;
    @ManyToOne @JoinColumn(name="id_characteristics") private Characteristics characteristics;
}