package tvz.zavrsni.earhiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvz.zavrsni.earhiv.entity.Datoteka;

public interface DatotekaRepository extends JpaRepository<Datoteka, Long> {
}
