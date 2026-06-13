package tvz.zavrsni.earhiv.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tvz.zavrsni.earhiv.service.RacunService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RacunService racunService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var nedavniRacuni = racunService.dohvatiByKorisnik(
                userDetails.getUsername(),
                PageRequest.of(0, 5, Sort.by("datumUcitavanja").descending())
        ).getContent();
        model.addAttribute("nedavniRacuni", nedavniRacuni);
        return "home";
    }
}
