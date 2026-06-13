package tvz.zavrsni.earhiv.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tvz.zavrsni.earhiv.dto.RacunRequestDto;
import tvz.zavrsni.earhiv.dto.RacunSazetakDto;
import tvz.zavrsni.earhiv.entity.Datoteka;
import tvz.zavrsni.earhiv.entity.Racun;
import tvz.zavrsni.earhiv.exception.ResourceNotFoundException;
import tvz.zavrsni.earhiv.repository.DatotekaRepository;
import tvz.zavrsni.earhiv.service.RacunService;

import java.util.List;

@Controller
@RequestMapping("/racuni")
@RequiredArgsConstructor
public class RacunWebController {

    private final RacunService racunService;
    private final DatotekaRepository datotekaRepository;

    @GetMapping
    public String lista(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<RacunSazetakDto> racuni = racunService.dohvatiByKorisnik(
                userDetails.getUsername(),
                PageRequest.of(0, 50, Sort.by("datumUcitavanja").descending())
        ).getContent().stream().map(this::toSazetakDto).toList();
        model.addAttribute("racuni", racuni);
        return "racuni/lista";
    }

    @GetMapping("/novi")
    public String noviForm(Model model) {
        model.addAttribute("racunRequestDto", new RacunRequestDto());
        return "racuni/forma";
    }

    @PostMapping
    public String spremi(@Valid @ModelAttribute RacunRequestDto racunRequestDto,
                         BindingResult bindingResult,
                         @RequestParam(value = "datoteke", required = false) List<MultipartFile> datoteke,
                         @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "racuni/forma";
        }
        racunRequestDto.setKorisnik(userDetails.getUsername());
        racunService.spremiRacun(racunRequestDto, datoteke);
        return "redirect:/racuni";
    }

    @GetMapping("/{id}")
    public String detalj(@PathVariable Long id, Model model) {
        Racun racun = racunService.dohvatiById(id);
        model.addAttribute("racun", racun);
        return "racuni/detalj";
    }

    @GetMapping("/datoteke/{datotekaId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long datotekaId) {
        Datoteka datoteka = datotekaRepository.findById(datotekaId)
                .orElseThrow(() -> new ResourceNotFoundException("Datoteka nije pronađena: " + datotekaId));
        byte[] bytes = racunService.dohvatiDatoteku(datotekaId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + datoteka.getNaziv() + "\"")
                .body(bytes);
    }

    @PostMapping("/{id}/obrisi")
    public String obrisi(@PathVariable Long id) {
        racunService.obrisi(id);
        return "redirect:/racuni";
    }

    private RacunSazetakDto toSazetakDto(Racun racun) {
        return RacunSazetakDto.builder()
                .id(racun.getId())
                .brojRacuna(racun.getBrojRacuna())
                .datumIzdavanja(racun.getDatumIzdavanja())
                .iznos(racun.getIznos())
                .valuta(racun.getValuta())
                .izdavatelj(racun.getIzdavatelj())
                .brojDatoteka(racun.getDatoteke().size())
                .build();
    }
}
