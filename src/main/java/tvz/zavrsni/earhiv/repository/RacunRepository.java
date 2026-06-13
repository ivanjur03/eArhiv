package tvz.zavrsni.earhiv.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tvz.zavrsni.earhiv.entity.Racun;

public interface RacunRepository extends JpaRepository<Racun, Long> {
    Page<Racun> findByKorisnik(String korisnik, Pageable pageable);
}
