package tvz.zavrsni.earhiv.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RacunResponseDto {
    private Long id;
    private String brojRacuna;
    private LocalDate datumIzdavanja;
    private LocalDate datumDospijeca;
    private BigDecimal iznos;
    private String valuta;
    private String izdavatelj;
    private String primatelj;
    private String opis;
    private LocalDateTime datumUcitavanja;
    private String korisnik;
    private List<DatotekaResponseDto> datoteke;
}
