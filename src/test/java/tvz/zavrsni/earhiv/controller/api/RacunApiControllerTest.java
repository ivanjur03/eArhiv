package tvz.zavrsni.earhiv.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tvz.zavrsni.earhiv.config.SecurityConfig;
import tvz.zavrsni.earhiv.entity.Racun;
import tvz.zavrsni.earhiv.exception.ResourceNotFoundException;
import tvz.zavrsni.earhiv.repository.DatotekaRepository;
import tvz.zavrsni.earhiv.service.RacunService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RacunApiController.class)
@Import(SecurityConfig.class)
class RacunApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RacunService racunService;

    @MockitoBean
    private DatotekaRepository datotekaRepository;

    @Test
    void detalj_bezAutentikacijeVraca401() throws Exception {
        mockMvc.perform(get("/api/racuni/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void detalj_saAutentikacijomVracaRacun() throws Exception {
        Racun racun = new Racun();
        racun.setId(1L);
        racun.setBrojRacuna("R-001");
        racun.setIzdavatelj("Firma d.o.o.");
        racun.setDatumIzdavanja(LocalDate.of(2024, 3, 15));
        racun.setIznos(BigDecimal.valueOf(100));
        racun.setKorisnik("ivan");
        when(racunService.dohvatiById(1L)).thenReturn(racun);

        mockMvc.perform(get("/api/racuni/1").with(user("ivan")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brojRacuna").value("R-001"))
                .andExpect(jsonPath("$.izdavatelj").value("Firma d.o.o."));
    }

    @Test
    void detalj_kadaRacunNePostojiVraca404() throws Exception {
        when(racunService.dohvatiById(99L))
                .thenThrow(new ResourceNotFoundException("Račun nije pronađen: 99"));

        mockMvc.perform(get("/api/racuni/99").with(user("ivan")))
                .andExpect(status().isNotFound());
    }
}