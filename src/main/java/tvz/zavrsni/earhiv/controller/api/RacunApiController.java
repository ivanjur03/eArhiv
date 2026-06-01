package tvz.zavrsni.earhiv.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tvz.zavrsni.earhiv.dto.DatotekaResponseDto;
import tvz.zavrsni.earhiv.dto.RacunRequestDto;
import tvz.zavrsni.earhiv.dto.RacunResponseDto;
import tvz.zavrsni.earhiv.dto.RacunSazetakDto;
import tvz.zavrsni.earhiv.entity.Datoteka;
import tvz.zavrsni.earhiv.entity.Racun;
import tvz.zavrsni.earhiv.exception.ResourceNotFoundException;
import tvz.zavrsni.earhiv.repository.DatotekaRepository;
import tvz.zavrsni.earhiv.service.RacunService;

import java.util.List;

@RestController
@RequestMapping("/api/racuni")
@RequiredArgsConstructor
public class RacunApiController {

    private final RacunService racunService;
    private final DatotekaRepository datotekaRepository;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public RacunResponseDto kreiraj(
            @RequestPart("podaci") RacunRequestDto podaci,
            @RequestPart("datoteke") List<MultipartFile> datoteke) {
        Racun racun = racunService.spremiRacun(podaci, datoteke);
        return toResponseDto(racun);
    }

    @GetMapping("/")
    public Page<RacunSazetakDto> lista(Pageable pageable) {
        return racunService.dohvatiSve(pageable).map(this::toSazetakDto);
    }

    @GetMapping("/{id}")
    public RacunResponseDto detalj(@PathVariable Long id) {
        return toResponseDto(racunService.dohvatiById(id));
    }

    @GetMapping("/datoteke/{datotekaId}/download")
    public ResponseEntity<byte[]> downloadDatoteka(@PathVariable Long datotekaId) {
        Datoteka datoteka = datotekaRepository.findById(datotekaId)
                .orElseThrow(() -> new ResourceNotFoundException("Datoteka nije pronađena: " + datotekaId));
        byte[] bytes = racunService.dohvatiDatoteku(datotekaId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + datoteka.getNaziv() + "\"")
                .body(bytes);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void obrisi(@PathVariable Long id) {
        racunService.obrisi(id);
    }

    private RacunResponseDto toResponseDto(Racun racun) {
        List<DatotekaResponseDto> datotekeDto = racun.getDatoteke().stream()
                .map(d -> DatotekaResponseDto.builder()
                        .id(d.getId())
                        .naziv(d.getNaziv())
                        .tip(d.getTip())
                        .velicina(d.getVelicina())
                        .datumUcitavanja(d.getDatumUcitavanja())
                        .build())
                .toList();
        return RacunResponseDto.builder()
                .id(racun.getId())
                .brojRacuna(racun.getBrojRacuna())
                .datumIzdavanja(racun.getDatumIzdavanja())
                .datumDospijeca(racun.getDatumDospijeca())
                .iznos(racun.getIznos())
                .valuta(racun.getValuta())
                .izdavatelj(racun.getIzdavatelj())
                .primatelj(racun.getPrimatelj())
                .opis(racun.getOpis())
                .datumUcitavanja(racun.getDatumUcitavanja())
                .korisnik(racun.getKorisnik())
                .datoteke(datotekeDto)
                .build();
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
