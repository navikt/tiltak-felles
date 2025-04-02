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
        List<HentPersonBolk> bolkListe = Optional.ofNullable(data())
            .flatMap(bolkData -> Optional.ofNullable(bolkData.hentPersonBolk()))
            .orElse(Collections.emptyList());

        Map<String, Optional<Diskresjonskode>> diskresjonskodeMap = bolkListe.stream()
            .filter(HentPersonBolk::isOk)
            .map(bolk -> Map.entry(
                bolk.ident(),
                PdlResponse.utledAdressebeskyttelse(bolk.person()).map(Adressebeskyttelse::gradering)
            ))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a.isEmpty() ? b : a
            ));

        return fnrSet.stream().collect(Collectors.toMap(
            fnr -> fnr,
            fnr -> diskresjonskodeMap.getOrDefault(fnr, Optional.empty())
        ));
    }
}
