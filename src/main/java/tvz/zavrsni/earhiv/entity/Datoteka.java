package tvz.zavrsni.earhiv.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "datoteka")
public class Datoteka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(nullable = false)
    private String tip;

    @Column(nullable = false)
    private String s3Kljuc;

    private Long velicina;

    private LocalDateTime datumUcitavanja;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "racun_id", nullable = false)
    private Racun racun;

    @PrePersist
    public void prePersist() {
        datumUcitavanja = LocalDateTime.now();
    }
}
