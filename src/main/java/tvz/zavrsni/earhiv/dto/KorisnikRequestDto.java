package tvz.zavrsni.earhiv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KorisnikRequestDto {

    @NotBlank(message = "Korisničko ime je obavezno")
    @Size(min = 3, max = 50, message = "Korisničko ime mora imati između 3 i 50 znakova")
    private String korisnickoIme;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 4, message = "Lozinka mora imati najmanje 4 znaka")
    private String lozinka;

    private String ime;
    private String prezime;

    @Email(message = "Email adresa nije ispravnog formata")
    private String email;

    private String uloga = "ROLE_USER";
}
