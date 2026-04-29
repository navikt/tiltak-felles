package no.nav.team_tiltak.felles.persondata.pdl.domene;

import java.util.List;
import java.util.Optional;

public record PdlResponseMedAdresse(Data data) {
    public record Data(HentPersonMedAdresse hentPerson) { }

    public record HentPersonMedAdresse(
        List<Adressebeskyttelse> adressebeskyttelse,
        List<Doedsfall> doedsfall,
        List<NavnMedMetadata> navn,
        List<Kontaktadresse> kontaktadresse,
        List<Oppholdsadresse> oppholdsadresse,
        List<KontaktinformasjonForDoedsbo> kontaktinformasjonForDoedsbo,
        List<Folkeregisteridentifikator> folkeregisteridentifikator,
        List<Bostedsadresse> bostedsadresse,
        List<Folkeregisterpersonstatus> folkeregisterpersonstatus
    ) { }

    public Optional<Navn> utledNavn() {
        return Optional.ofNullable(data())
            .map(Data::hentPerson)
            .flatMap(person -> first(person.navn()))
            .map(NavnMedMetadata::tilNavn);
    }

    public Optional<Diskresjonskode> utledDiskresjonskode() {
        return Optional.ofNullable(data())
            .map(Data::hentPerson)
            .flatMap(person -> first(person.adressebeskyttelse()))
            .map(Adressebeskyttelse::gradering);
    }

    private static <T> Optional<T> first(List<T> values) {
        return Optional.ofNullable(values)
            .filter(list -> !list.isEmpty())
            .map(List::getFirst);
    }

    public record Doedsfall(String doedsdato) { }

    public record NavnMedMetadata(
        String fornavn,
        String mellomnavn,
        String etternavn,
        String forkortetNavn,
        String gyldigFraOgMed,
        Metadata metadata
    ) {
        public Navn tilNavn() {
            return new Navn(fornavn, mellomnavn, etternavn);
        }
    }

    public record Kontaktadresse(
        String gyldigFraOgMed,
        String gyldigTilOgMed,
        String type,
        String coAdressenavn,
        Postboksadresse postboksadresse,
        Vegadresse vegadresse,
        PostadresseIFrittFormat postadresseIFrittFormat,
        UtenlandskAdresse utenlandskAdresse,
        UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat,
        Metadata metadata
    ) { }

    public record Oppholdsadresse(
        String gyldigFraOgMed,
        String gyldigTilOgMed,
        String coAdressenavn,
        UtenlandskAdresse utenlandskAdresse,
        Vegadresse vegadresse,
        Metadata metadata,
        Matrikkeladresse matrikkeladresse,
        String oppholdAnnetSted
    ) { }

    public record KontaktinformasjonForDoedsbo(
        String skifteform,
        String attestutstedelsesdato,
        PersonSomKontakt personSomKontakt,
        AdvokatSomKontakt advokatSomKontakt,
        OrganisasjonSomKontakt organisasjonSomKontakt,
        DoedsboAdresse adresse,
        Metadata metadata
    ) { }

    public record PersonSomKontakt(
        String foedselsdato,
        Navn personnavn,
        String identifikasjonsnummer
    ) { }

    public record AdvokatSomKontakt(
        Navn personnavn,
        String organisasjonsnavn,
        String organisasjonsnummer
    ) { }

    public record OrganisasjonSomKontakt(
        Navn kontaktperson,
        String organisasjonsnavn,
        String organisasjonsnummer
    ) { }

    public record DoedsboAdresse(
        String adresselinje1,
        String adresselinje2,
        String poststedsnavn,
        String postnummer,
        String landkode
    ) { }

    public record Folkeregisteridentifikator(
        String identifikasjonsnummer,
        String type,
        String status
    ) { }

    public record Bostedsadresse(
        String angittFlyttedato,
        String gyldigFraOgMed,
        String gyldigTilOgMed,
        String coAdressenavn,
        Vegadresse vegadresse,
        UtenlandskAdresse utenlandskAdresse,
        Matrikkeladresse matrikkeladresse,
        UkjentBosted ukjentBosted,
        Metadata metadata
    ) { }

    public record Folkeregisterpersonstatus(
        String status,
        String forenkletStatus,
        Folkeregistermetadata folkeregistermetadata
    ) { }

    public record Folkeregistermetadata(String kilde) { }

    public record Postboksadresse(
        String postbokseier,
        String postboks,
        String postnummer
    ) { }

    public record Vegadresse(
        String matrikkelId,
        String husnummer,
        String husbokstav,
        String bruksenhetsnummer,
        String adressenavn,
        String kommunenummer,
        String bydelsnummer,
        String tilleggsnavn,
        String postnummer
    ) { }

    public record PostadresseIFrittFormat(
        String adresselinje1,
        String adresselinje2,
        String adresselinje3,
        String postnummer
    ) { }

    public record UtenlandskAdresse(
        String adressenavnNummer,
        String bygningEtasjeLeilighet,
        String postboksNummerNavn,
        String postkode,
        String bySted,
        String regionDistriktOmraade,
        String landkode
    ) { }

    public record UtenlandskAdresseIFrittFormat(
        String adresselinje1,
        String adresselinje2,
        String adresselinje3,
        String postkode,
        String byEllerStedsnavn,
        String landkode
    ) { }

    public record Matrikkeladresse(
        String matrikkelId,
        String bruksenhetsnummer,
        String tilleggsnavn,
        String postnummer,
        String kommunenummer
    ) { }

    public record UkjentBosted(String bostedskommune) { }

    public record Metadata(
        String master,
        List<Endring> endringer,
        Boolean historisk
    ) { }

    public record Endring(
        String type,
        String registrert,
        String registrertAv,
        String systemkilde,
        String kilde
    ) { }
}

