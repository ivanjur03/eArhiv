package tvz.zavrsni.earhiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tvz.zavrsni.earhiv.entity.KorisnikDetalji;
import tvz.zavrsni.earhiv.repository.KorisnikDetaljiRepository;

@Service
@RequiredArgsConstructor
public class KorisnikDetaljiService implements UserDetailsService {

    private final KorisnikDetaljiRepository korisnikDetaljiRepository;

    /**
     * Učitava korisnika iz baze i mapira ga u Spring Security {@link UserDetails}, s {@code uloga} kao authority.
     *
     * @param username korisničko ime
     * @return podaci o korisniku za autentikaciju
     * @throws UsernameNotFoundException ako korisnik ne postoji
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        KorisnikDetalji korisnik = korisnikDetaljiRepository.findByKorisnickoIme(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));

        String uloga = korisnik.getUloga();

        return User.builder()
                .username(korisnik.getKorisnickoIme())
                .password(korisnik.getLozinka())
                .authorities(uloga)
                .build();
    }
}
