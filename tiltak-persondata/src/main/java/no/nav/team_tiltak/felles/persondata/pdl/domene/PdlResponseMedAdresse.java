package no.nav.team_tiltak.felles.persondata.pdl.domene;

import java.util.List;
import java.util.Optional;


/**
 * Adresser brukes til flere ulike formål. Man bør derfor sette seg inn i dokumentasjonen om adresser før man velger
 * hvilke adresser man vil hente. Hvor man er bosatt og hvor man oppholder seg vil være relevant i mange vurderinger og saksbehandling.
 * Da er andre adressetyper viktigere enn kontaktadressen, som er mest aktuell ved utsendelse av post.
 * Når en adresse ble registrert og hvem som registrerte den kan også ha betydning for hvilken adresse man skal velge.
 * For noen formål er også master og kilde avgjørende for valg av adresse.
 *
 * Siden det er ulike formål og behov i etaten er det ikke hensiktsmessig å ha en prioritert adresse som skal brukes av alle i alle sammenhenger.
 * Selv om det er mulig å ha opptil seks adresser på samme person, vil det være uvanlig at man har flere enn to eller tre. Og flertallet av Navs brukere vil kun ha bostedsadresse.
 *
 * Adresser til post
 * Dersom formålet er å sende ut noe i post til bruker, vil vi anbefale følgende prioritering:
 *
 * 1. Kontaktadresse med master PDL
 * 2. Kontaktadresse fra Freg med nyeste registreringsdato (det er mulig med to)
 * 3. Oppholdsadresse med master PDL
 * 4. Oppholdsadresse med master Freg (Ikke aktuell om den kun inneholder oppholdAnnetSted)
 * 5. Bostedsadresse
 *
 * NB! Dersom personen har en utenlandsk bostedsadresse som er nyere enn den adressen som velges ved prioriteringen ovenfor her,
 * anbefaler vi at bostedsadressen benyttes. Utenlandsk bostedsadresse har ikke alltid en dato i gyldigFraOgMed,
 * da kan man bruke dato i metadata registrert for å sjekke hvilken adresse som er sist registrert.
 *
 * https://pdl-docs.ansatt.nav.no/ekstern/index.html#_hvilken_adresse_b%C3%B8r_man_bruke
 * */
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

