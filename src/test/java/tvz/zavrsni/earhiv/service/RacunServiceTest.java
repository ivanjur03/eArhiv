package tvz.zavrsni.earhiv.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tvz.zavrsni.earhiv.entity.Datoteka;
import tvz.zavrsni.earhiv.entity.Racun;
import tvz.zavrsni.earhiv.exception.ResourceNotFoundException;
import tvz.zavrsni.earhiv.repository.DatotekaRepository;
import tvz.zavrsni.earhiv.repository.RacunRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RacunServiceTest {

    @Mock
    private RacunRepository racunRepository;

    @Mock
    private DatotekaRepository datotekaRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private RacunService racunService;

    @Test
    void dohvatiById_vracaRacunKadaPostoji() {
        Racun racun = new Racun();
        racun.setId(1L);
        racun.setBrojRacuna("R-001");
        when(racunRepository.findById(1L)).thenReturn(Optional.of(racun));

        Racun rezultat = racunService.dohvatiById(1L);

        assertThat(rezultat.getBrojRacuna()).isEqualTo("R-001");
    }

    @Test
    void dohvatiById_baciIznimkuKadaNePostoji() {
        when(racunRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> racunService.dohvatiById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void dohvatiByKorisnik_delegiraNaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Racun racun = new Racun();
        racun.setKorisnik("ivan");
        Page<Racun> stranica = new PageImpl<>(List.of(racun));
        when(racunRepository.findByKorisnik("ivan", pageable)).thenReturn(stranica);

        Page<Racun> rezultat = racunService.dohvatiByKorisnik("ivan", pageable);

        assertThat(rezultat.getContent()).hasSize(1);
        assertThat(rezultat.getContent().getFirst().getKorisnik()).isEqualTo("ivan");
    }

    @Test
    void obrisi_brisesDatotekeSaS3IRacunIzBaze() {
        Racun racun = new Racun();
        racun.setId(1L);
        Datoteka datoteka = new Datoteka();
        datoteka.setS3Kljuc("racuni/ivan/1/datoteka.pdf");
        racun.setDatoteke(List.of(datoteka));
        when(racunRepository.findById(1L)).thenReturn(Optional.of(racun));

        racunService.obrisi(1L);

        verify(s3Service, times(1)).delete("racuni/ivan/1/datoteka.pdf");
        verify(racunRepository, times(1)).delete(racun);
    }
}
