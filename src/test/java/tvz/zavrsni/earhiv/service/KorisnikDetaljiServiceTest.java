package tvz.zavrsni.earhiv.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tvz.zavrsni.earhiv.entity.KorisnikDetalji;
import tvz.zavrsni.earhiv.repository.KorisnikDetaljiRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KorisnikDetaljiServiceTest {

    @Mock
    private KorisnikDetaljiRepository korisnikDetaljiRepository;

    @InjectMocks
    private KorisnikDetaljiService korisnikDetaljiService;

    @Test
    void loadUserByUsername_vracaKorisnikaKadaPostoji() {
        KorisnikDetalji korisnik = new KorisnikDetalji();
        korisnik.setKorisnickoIme("admin");
        korisnik.setLozinka("hashed-lozinka");
        korisnik.setUloga("ROLE_ADMIN");
        when(korisnikDetaljiRepository.findByKorisnickoIme("admin")).thenReturn(Optional.of(korisnik));

        UserDetails rezultat = korisnikDetaljiService.loadUserByUsername("admin");

        assertThat(rezultat.getUsername()).isEqualTo("admin");
        assertThat(rezultat.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_baciIznimkuKadaKorisnikNePostoji() {
        when(korisnikDetaljiRepository.findByKorisnickoIme("nepostojeci")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> korisnikDetaljiService.loadUserByUsername("nepostojeci"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
