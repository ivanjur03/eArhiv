package tvz.zavrsni.earhiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvz.zavrsni.earhiv.entity.Racun;

public interface RacunRepository extends JpaRepository<Racun, Long> {
}
