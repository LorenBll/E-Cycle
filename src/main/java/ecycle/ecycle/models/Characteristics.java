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
    @Column(name="main_colour", length=10) private String main_colour;
    @Column(name="function", length=50) private String function;
    @Column(name="quality") private String quality;
    @Column(name="prod_year") private int prod_year;
    @ManyToOne @JoinColumn(name="id_category") private Category category;
    @ManyToOne @JoinColumn(name="id_model") private Model model;
    @ManyToOne @JoinColumn(name="id_nature") private Nature nature;

}