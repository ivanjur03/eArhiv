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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        KorisnikDetalji korisnik = korisnikDetaljiRepository.findByKorisnickoIme(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));
        return User.builder()
                .username(korisnik.getKorisnickoIme())
                .password(korisnik.getLozinka())
                .roles("USER")
                .build();
    }
}
