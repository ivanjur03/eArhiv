package tvz.zavrsni.earhiv.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "racun")
public class Racun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brojRacuna;

    @Column(nullable = false)
    private LocalDate datumIzdavanja;

    private LocalDate datumDospijeca;

    @Column(nullable = false)
    private BigDecimal iznos;

    @Column(length = 10)
    private String valuta = "EUR";

    @Column(nullable = false)
    private String izdavatelj;

    private String primatelj;

    @Column(length = 1000)
    private String opis;

    private LocalDateTime datumUcitavanja;

    @Column(nullable = false)
    private String korisnik;

    @ToString.Exclude
    @OneToMany(mappedBy = "racun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Datoteka> datoteke = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        datumUcitavanja = LocalDateTime.now();
    }
}
