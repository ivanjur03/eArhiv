package tvz.zavrsni.earhiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvz.zavrsni.earhiv.entity.KorisnikDetalji;

import java.util.Optional;

public interface KorisnikDetaljiRepository extends JpaRepository<KorisnikDetalji, Long> {
    Optional<KorisnikDetalji> findByKorisnickoIme(String korisnickoIme);
}
