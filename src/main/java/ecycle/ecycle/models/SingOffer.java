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
@Table(name="sing_offers")
public class SingOffer {
    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="price") private float price;
    @Column(name="picture_path",length=255) private String picturePath;
    @Column(name="description",length=255) private String description;    
    @Column(name="expiration") private Date expiration;
    @Column(name="ts_deletion") private Timestamp tsDeletion;
    @ManyToOne @JoinColumn(name="id_offer") private Interaction offer;
    @ManyToOne @JoinColumn(name="id_characteristics") private Characteristics characteristics;
}
