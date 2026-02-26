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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="characteristics")
public class Characteristics { 
    @Id @Column(name="ID") @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(name="main_colour",length=50) private String mainColour;
    @Column(name="function",length=50) private String function;
    @Column(name="quality",length=50) private String quality;    
    @Column(name="prod_year") private int prodYear;
    @Column(name="batch",length=50) private String batch;
    @ManyToOne @JoinColumn(name="id_model") private ProductModel model;
    @ManyToOne @JoinColumn(name="id_category") private Category category;
    @ManyToOne @JoinColumn(name="id_nature") private Nature nature;
}
