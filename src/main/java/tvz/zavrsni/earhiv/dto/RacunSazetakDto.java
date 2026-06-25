package tvz.zavrsni.earhiv.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RacunSazetakDto {
    private Long id;
    private String brojRacuna;
    private LocalDate datumIzdavanja;
    private BigDecimal iznos;
    private String valuta;
    private String izdavatelj;
    private String primatelj;
    private int brojDatoteka;
}
