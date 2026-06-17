package tvz.zavrsni.earhiv.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Tag(name = "Računi", description = "Upravljanje e-računima i priloženim datotekama")
@RestController
@RequestMapping("/api/racuni")
@RequiredArgsConstructor
public class RacunApiController {

    private final RacunService racunService;
    private final DatotekaRepository datotekaRepository;

    @Operation(summary = "Kreiraj novi račun", description = "Pohrani novi račun s opcionalnim priloženim datotekama na S3")
    @ApiResponse(responseCode = "201", description = "Račun uspješno kreiran")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public RacunResponseDto kreiraj(
            @RequestPart("podaci") RacunRequestDto podaci,
            @RequestPart("datoteke") List<MultipartFile> datoteke) {
        Racun racun = racunService.spremiRacun(podaci, datoteke);
        return toResponseDto(racun);
    }

    @Operation(summary = "Lista svih računa", description = "Straničena lista svih računa u arhivi")
    @GetMapping("/")
    public Page<RacunSazetakDto> lista(
            @Parameter(description = "Broj stranice") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Veličina stranice") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Polje za sortiranje, npr. datumUcitavanja,desc") @RequestParam(defaultValue = "id,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));
        return racunService.dohvatiSve(pageable).map(this::toSazetakDto);
    }

    @Operation(summary = "Detalji računa")
    @ApiResponse(responseCode = "404", description = "Račun nije pronađen")
    @GetMapping("/{id}")
    public RacunResponseDto detalj(@Parameter(description = "ID računa") @PathVariable Long id) {
        return toResponseDto(racunService.dohvatiById(id));
    }

    @Operation(summary = "Preuzmi datoteku", description = "Preuzmi priloženu datoteku iz S3 pohrane")
    @ApiResponse(responseCode = "404", description = "Datoteka nije pronađena")
    @GetMapping("/datoteke/{datotekaId}/download")
    public ResponseEntity<byte[]> downloadDatoteka(
            @Parameter(description = "ID datoteke") @PathVariable Long datotekaId) {
        Datoteka datoteka = datotekaRepository.findById(datotekaId)
                .orElseThrow(() -> new ResourceNotFoundException("Datoteka nije pronađena: " + datotekaId));
        byte[] bytes = racunService.dohvatiDatoteku(datotekaId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + datoteka.getNaziv() + "\"")
                .body(bytes);
    }

    @Operation(summary = "Obriši račun", description = "Briše račun i sve priložene datoteke iz S3 pohrane")
    @ApiResponse(responseCode = "204", description = "Račun uspješno obrisan")
    @ApiResponse(responseCode = "404", description = "Račun nije pronađen")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void obrisi(@Parameter(description = "ID računa") @PathVariable Long id) {
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
