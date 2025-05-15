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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="negotiations")

public class Negotiation {    

    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="ts_creation") private Timestamp ts_creation;
    @Column(name="ts_update") private Timestamp ts_update;
    @Column(name="ts_closure") private Timestamp ts_closure;
    @Column(name="closure") private String closure;
    @ManyToOne @JoinColumn(name="id_request") private Request request;
    @ManyToOne @JoinColumn(name="id_asset") private Asset asset;

}