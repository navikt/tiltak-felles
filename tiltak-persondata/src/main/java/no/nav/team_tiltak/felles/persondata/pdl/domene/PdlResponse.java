package no.nav.team_tiltak.felles.persondata.pdl.domene;


import java.util.NoSuchElementException;
import java.util.Optional;

public record PdlResponse(Data data) {
    public record Data(
        HentPerson hentPerson,
        HentIdenter hentIdenter,
        HentGeografiskTilknytning hentGeografiskTilknytning
    ) { }

    public Optional<String> utledGeoLokasjon() {
        try {
            return Optional.of(data().hentGeografiskTilknytning().getGeoTilknytning());
        } catch (NullPointerException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Optional<String> utledGjeldendeIdent() {
        try {
            return data().hentIdenter().identer().stream()
                .filter(i -> !i.historisk())
                .map(Identer::ident)
                .findFirst();
        } catch (NullPointerException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Optional<Navn> utledNavn() {
        try {
            return Optional.of(data().hentPerson().navn().getFirst());
        } catch (NullPointerException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Optional<Diskresjonskode> utledDiskresjonskode() {
        return Optional.ofNullable(data())
            .flatMap(data -> utledAdressebeskyttelse(data.hentPerson()))
            .map(Adressebeskyttelse::gradering);
    }

    public static Optional<Adressebeskyttelse> utledAdressebeskyttelse(HentPerson hentPerson) {
        try {
            return Optional.of(hentPerson.adressebeskyttelse().getFirst());
        } catch (NullPointerException | NoSuchElementException e) {
            return Optional.empty();
        }
    }
}
