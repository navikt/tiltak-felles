package no.nav.team_tiltak.felles.persondata.pdl.domene;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record PdlResponseBolk(Data data) {
    public record Data(List<HentPersonBolk> hentPersonBolk) {}

    public Map<String, Optional<Diskresjonskode>> utledDiskresjonskoder(Set<String> fnrSet) {
        List<HentPersonBolk> bolkListe = hentPersonBolk();

        Map<String, Diskresjonskode> diskresjonskodeMap = bolkListe.stream()
            .filter(HentPersonBolk::isOk)
            .map(bolk -> Map.entry(
                bolk.ident(),
                PdlResponse.utledAdressebeskyttelse(bolk.person())
                    .map(Adressebeskyttelse::gradering)
                    .orElse(Diskresjonskode.UGRADERT)
            ))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));

        return fnrSet.stream().collect(Collectors.toMap(
            fnr -> fnr,
            fnr -> Optional.ofNullable(diskresjonskodeMap.get(fnr))
        ));
    }

    public List<HentPersonBolk> hentPersonBolk() {
        return Optional.ofNullable(data())
            .flatMap(bolkData -> Optional.ofNullable(bolkData.hentPersonBolk()))
            .orElse(Collections.emptyList());
    }
}
