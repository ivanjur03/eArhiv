package tvz.zavrsni.earhiv.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "korisnik_detalji")
public class KorisnikDetalji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String korisnickoIme;

    @Column(nullable = false)
    private String lozinka;

    private String ime;

    private String prezime;

    private String email;

    @Column(nullable = false)
    private boolean aktivan = true;

    @Column(nullable = false)
    private String uloga = "ROLE_USER";
}
