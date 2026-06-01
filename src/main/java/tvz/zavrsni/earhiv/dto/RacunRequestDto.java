package tvz.zavrsni.earhiv.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RacunRequestDto {
    private String brojRacuna;
    private LocalDate datumIzdavanja;
    private LocalDate datumDospijeca;
    private BigDecimal iznos;
    private String valuta;
    private String izdavatelj;
    private String primatelj;
    private String opis;
    private String korisnik;
}
