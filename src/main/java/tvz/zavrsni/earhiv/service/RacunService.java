package tvz.zavrsni.earhiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tvz.zavrsni.earhiv.dto.RacunRequestDto;
import tvz.zavrsni.earhiv.entity.Datoteka;
import tvz.zavrsni.earhiv.entity.Racun;
import tvz.zavrsni.earhiv.exception.ResourceNotFoundException;
import tvz.zavrsni.earhiv.repository.DatotekaRepository;
import tvz.zavrsni.earhiv.repository.RacunRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RacunService {

    private final RacunRepository racunRepository;
    private final DatotekaRepository datotekaRepository;
    private final S3Service s3Service;

    @Transactional
    public Racun spremiRacun(RacunRequestDto dto, List<MultipartFile> datoteke) {
        Racun racun = new Racun();
        racun.setBrojRacuna(dto.getBrojRacuna());
        racun.setDatumIzdavanja(dto.getDatumIzdavanja());
        racun.setDatumDospijeca(dto.getDatumDospijeca());
        racun.setIznos(dto.getIznos());
        racun.setValuta(dto.getValuta() != null && !dto.getValuta().isBlank() ? dto.getValuta() : "EUR");
        racun.setIzdavatelj(dto.getIzdavatelj());
        racun.setPrimatelj(dto.getPrimatelj());
        racun.setOpis(dto.getOpis());
        racun.setKorisnik(dto.getKorisnik());

        Racun savedRacun = racunRepository.save(racun);

        if (datoteke != null) {
            for (MultipartFile file : datoteke) {
                if (file == null || file.isEmpty()) continue;
                try {
                    String key = "racuni/" + savedRacun.getKorisnik() + "/" + savedRacun.getId()
                            + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
                    s3Service.upload(key, file.getInputStream(), file.getSize(), file.getContentType());

                    Datoteka datoteka = new Datoteka();
                    datoteka.setNaziv(file.getOriginalFilename());
                    datoteka.setTip(file.getContentType());
                    datoteka.setS3Kljuc(key);
                    datoteka.setVelicina(file.getSize());
                    datoteka.setRacun(savedRacun);
                    datotekaRepository.save(datoteka);
                } catch (Exception e) {
                    throw new RuntimeException("Greška pri pohrani datoteke: " + file.getOriginalFilename(), e);
                }
            }
        }

        return racunRepository.findById(savedRacun.getId()).orElseThrow();
    }

    public Page<Racun> dohvatiSve(Pageable pageable) {
        return racunRepository.findAll(pageable);
    }

    public Racun dohvatiById(Long id) {
        return racunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Račun nije pronađen: " + id));
    }

    public byte[] dohvatiDatoteku(Long datotekaId) {
        Datoteka datoteka = datotekaRepository.findById(datotekaId)
                .orElseThrow(() -> new ResourceNotFoundException("Datoteka nije pronađena: " + datotekaId));
        return s3Service.download(datoteka.getS3Kljuc());
    }

    public Page<Racun> dohvatiByKorisnik(String korisnik, Pageable pageable) {
        return racunRepository.findByKorisnik(korisnik, pageable);
    }

    @Transactional
    public void obrisi(Long id) {
        Racun racun = dohvatiById(id);
        racun.getDatoteke().forEach(d -> s3Service.delete(d.getS3Kljuc()));
        racunRepository.delete(racun);
    }
}
