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
import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="requests")

public class Request {    

    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="ts_creation") private Timestamp ts_creation;
    @Column(name="ts_deletion") private Timestamp ts_deletion;
    @ManyToOne @JoinColumn(name="id_user") private User user;

}