package tvz.zavrsni.earhiv.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DatotekaResponseDto {
    private Long id;
    private String naziv;
    private String tip;
    private Long velicina;
    private LocalDateTime datumUcitavanja;
}
