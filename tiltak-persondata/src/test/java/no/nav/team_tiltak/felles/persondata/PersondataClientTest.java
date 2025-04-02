package no.nav.team_tiltak.felles.persondata;

import no.nav.team_tiltak.felles.persondata.pdl.PdlClient;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Adressebeskyttelse;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.HentGeografiskTilknytning;
import no.nav.team_tiltak.felles.persondata.pdl.domene.HentPerson;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Navn;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PersondataClientTest {
    private static final String STRENGT_FORTROLIG_PERSON = "16053900422";
    private static final String STRENGT_FORTROLIG_UTLAND_PERSON = "28033114267";
    private static final String FORTROLIG_PERSON = "26067114433";
    private static final String UGRADERT_PERSON_TOM_RESPONSE = "27030960020";
    private static final String USPESIFISERT_GRADERT_PERSON = "18076641842";
    private static final String PERSON_FINNES_IKKE = "24080687881";
    private static final String PERSON_FOR_RESPONS_UTEN_DATA = "23097010706";
    private static final String DONALD_DUCK = "00000000000";

    private PersondataClient persondataClient;

    @BeforeEach
    void setUp() {
        PdlClient pdlClient = mock(PdlClient.class);
        this.persondataClient = new PersondataClient(pdlClient);

        when(pdlClient.hentPersondata(DONALD_DUCK)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(
                new HentPerson(
                    List.of(new Adressebeskyttelse(Diskresjonskode.UGRADERT)),
                    List.of(new Navn("Donald", null, "Duck"))
                ),
                null,
                new HentGeografiskTilknytning("123", "030104", "5678", null)
            )))
        );

        when(pdlClient.hentPersondata(STRENGT_FORTROLIG_PERSON)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(
                new HentPerson(
                    List.of(new Adressebeskyttelse(Diskresjonskode.STRENGT_FORTROLIG)),
                    null
                ),
                null,
                null
            )))
        );

        when(pdlClient.hentPersondata(STRENGT_FORTROLIG_UTLAND_PERSON)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(
                new HentPerson(
                    List.of(new Adressebeskyttelse(Diskresjonskode.STRENGT_FORTROLIG_UTLAND)),
                    null
                ),
                null,
                null
            )))
        );

        when(pdlClient.hentPersondata(FORTROLIG_PERSON)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(
                new HentPerson(
                    List.of(new Adressebeskyttelse(Diskresjonskode.FORTROLIG)),
                    null
                ),
                null,
                null
            )))
        );
    }

    @Test
    public void hentNavn__tomt_navn_hvis_person_ikke_finens() {
        assertThat(persondataClient.hentNavn(PERSON_FINNES_IKKE)).isEqualTo(Navn.TOMT_NAVN);
    }

    @Test
    public void hentNavn__navn_hvis_person_finnes() {
        assertThat(persondataClient.hentNavn(DONALD_DUCK)).isEqualTo(new Navn("Donald", null, "Duck"));
    }

    @Test
    public void erKode6__strengt_fortrolig() {
        assertThat(persondataClient.hentDiskresjonskode(STRENGT_FORTROLIG_PERSON).erKode6()).isTrue();
    }

    @Test
    public void erKode6__strengt_fortrolig_utland() {
        assertThat(persondataClient.hentDiskresjonskode(STRENGT_FORTROLIG_UTLAND_PERSON).erKode6()).isTrue();
    }

    @Test
    public void erKode6__fortrolig() {
        assertThat(persondataClient.hentDiskresjonskode(FORTROLIG_PERSON).erKode6()).isFalse();
    }

    @Test
    public void erKode6__ugradert() {
        assertThat(persondataClient.hentDiskresjonskode(DONALD_DUCK).erKode6()).isFalse();
    }

    @Test
    public void erKode6__ugradertTom() {
        assertThat(persondataClient.hentDiskresjonskode(UGRADERT_PERSON_TOM_RESPONSE).erKode6()).isFalse();
    }

    @Test
    public void erKode6__uspesifisert_gradering() {
        assertThat(persondataClient.hentDiskresjonskode(USPESIFISERT_GRADERT_PERSON).erKode6()).isFalse();
    }

    @Test
    public void erKode6_person_finnes_ikke_er_ok() {
        assertThat(persondataClient.hentDiskresjonskode(PERSON_FINNES_IKKE).erKode6()).isFalse();
    }

    @Test
    public void henterGeoTilhørighet() {
        assertThat(persondataClient.hentGeografiskTilknytning(DONALD_DUCK).orElse(null)).isEqualTo("030104");
    }

}
