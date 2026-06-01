package tvz.zavrsni.earhiv.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tvz.zavrsni.earhiv.service.RacunService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RacunService racunService;

    @GetMapping("/")
    public String home(Model model) {
        var recentniRacuni = racunService.dohvatiSve(
                PageRequest.of(0, 5, Sort.by("datumUcitavanja").descending())
        ).getContent();
        model.addAttribute("nedavniRacuni", recentniRacuni);
        return "home";
    }
}
