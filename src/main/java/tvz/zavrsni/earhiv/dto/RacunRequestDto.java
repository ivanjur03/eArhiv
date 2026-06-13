package tvz.zavrsni.earhiv.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RacunRequestDto {

    @NotBlank(message = "Broj računa je obavezan")
    private String brojRacuna;

    @NotNull(message = "Datum izdavanja je obavezan")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datumIzdavanja;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datumDospijeca;

    @NotNull(message = "Iznos je obavezan")
    @DecimalMin(value = "0.01", message = "Iznos mora biti veći od 0")
    private BigDecimal iznos;

    private String valuta;

    @NotBlank(message = "Izdavatelj je obavezan")
    private String izdavatelj;

    private String primatelj;
    private String opis;
    private String korisnik;
}
