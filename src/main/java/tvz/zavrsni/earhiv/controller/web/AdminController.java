package tvz.zavrsni.earhiv.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tvz.zavrsni.earhiv.dto.KorisnikRequestDto;
import tvz.zavrsni.earhiv.entity.KorisnikDetalji;
import tvz.zavrsni.earhiv.repository.KorisnikDetaljiRepository;
import tvz.zavrsni.earhiv.service.RacunService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RacunService racunService;
    private final KorisnikDetaljiRepository korisnikDetaljiRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "20") int broj) {
        var nedavniUploadi = racunService.dohvatiSve(
                PageRequest.of(0, broj, Sort.by("datumUcitavanja").descending())
        ).getContent();
        var korisnici = korisnikDetaljiRepository.findAll();
        model.addAttribute("nedavniUploadi", nedavniUploadi);
        model.addAttribute("korisnici", korisnici);
        return "admin/dashboard";
    }

    @GetMapping("/korisnici/{korisnickoIme}")
    public String povijestKorisnika(@PathVariable String korisnickoIme, Model model) {
        var racuni = racunService.dohvatiByKorisnik(
                korisnickoIme,
                PageRequest.of(0, 100, Sort.by("datumUcitavanja").descending())
        ).getContent(); //ambiciozno stavljeno 100
        model.addAttribute("korisnickoIme", korisnickoIme);
        model.addAttribute("racuni", racuni);
        return "admin/korisnik-povijest";
    }

    @GetMapping("/novi-korisnik")
    public String noviKorisnikForm(Model model) {
        model.addAttribute("korisnikRequestDto", new KorisnikRequestDto());
        return "admin/novi-korisnik";
    }

    @PostMapping("/novi-korisnik")
    public String kreirajKorisnika(@Valid @ModelAttribute KorisnikRequestDto dto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/novi-korisnik";
        }
        KorisnikDetalji korisnik = new KorisnikDetalji();
        korisnik.setKorisnickoIme(dto.getKorisnickoIme());
        korisnik.setLozinka(passwordEncoder.encode(dto.getLozinka()));
        korisnik.setIme(dto.getIme());
        korisnik.setPrezime(dto.getPrezime());
        korisnik.setEmail(dto.getEmail());
        korisnik.setUloga(dto.getUloga() != null ? dto.getUloga() : "ROLE_USER");
        korisnik.setAktivan(true);
        korisnikDetaljiRepository.save(korisnik);
        return "redirect:/admin";
    }
}
