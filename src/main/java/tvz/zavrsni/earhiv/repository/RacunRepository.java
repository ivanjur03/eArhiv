package tvz.zavrsni.earhiv.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tvz.zavrsni.earhiv.entity.Racun;

public interface RacunRepository extends JpaRepository<Racun, Long> {
    Page<Racun> findByKorisnik(String korisnik, Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.velicina), 0) FROM Datoteka d WHERE d.racun.korisnik = :korisnik")
    long ukupnoBajtovaZaKorisnika(@Param("korisnik") String korisnik);
}
