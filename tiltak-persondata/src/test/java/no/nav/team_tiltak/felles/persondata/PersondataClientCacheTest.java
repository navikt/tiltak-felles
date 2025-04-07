package no.nav.team_tiltak.felles.persondata;

import no.nav.team_tiltak.felles.persondata.pdl.PdlClient;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Adressebeskyttelse;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.HentPerson;
import no.nav.team_tiltak.felles.persondata.pdl.domene.HentPersonBolk;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponseBolk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersondataClientCacheTest {
    private static String STRENG_FORTROLIG_FNR = "00000000000";
    private static HentPerson STRENG_FORTROLIG_PERSON = new HentPerson(
        List.of(new Adressebeskyttelse(Diskresjonskode.STRENGT_FORTROLIG)),
        null
    );
    private static String FORTROLIG_FNR = "00000000001";
    private static HentPerson FORTROLIG_PERSON = new HentPerson(
        List.of(new Adressebeskyttelse(Diskresjonskode.FORTROLIG)),
        null
    );
    private static String UGRADERT_FNR = "00000000002";
    private static HentPerson UGRADERT_PERSON = new HentPerson(
        List.of(new Adressebeskyttelse(Diskresjonskode.UGRADERT)),
        null
    );
    private static String TOM_RESPONS_FNR = "00000000003";
    private static HentPerson TOM_RESPONS_PERSON = new HentPerson(
        Collections.emptyList(),
        null
    );

    private PersondataClient persondataClient;
    private PdlClient pdlClient;

    @BeforeEach
    void setUp() {
        this.pdlClient = mock(PdlClient.class);
        this.persondataClient = new PersondataClient(pdlClient);

        when(pdlClient.hentPersondata(STRENG_FORTROLIG_FNR)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(STRENG_FORTROLIG_PERSON, null, null)))
        );

        when(pdlClient.hentPersondata(TOM_RESPONS_FNR)).thenReturn(
            Optional.of(new PdlResponse(new PdlResponse.Data(TOM_RESPONS_PERSON, null, null)))
        );

        when(pdlClient.hentPersonBolk(any())).thenReturn(
            Optional.of(
                new PdlResponseBolk(
                    new PdlResponseBolk.Data(
                        List.of(
                            new HentPersonBolk(STRENG_FORTROLIG_FNR, STRENG_FORTROLIG_PERSON, HentPersonBolk.OK),
                            new HentPersonBolk(FORTROLIG_FNR, FORTROLIG_PERSON, HentPersonBolk.OK),
                            new HentPersonBolk(UGRADERT_FNR, UGRADERT_PERSON, HentPersonBolk.OK),
                            new HentPersonBolk(TOM_RESPONS_FNR, TOM_RESPONS_PERSON, HentPersonBolk.OK)
                        )
                    )
                )
            )
        );
    }

    @Test
    void hentDiskresjonskode__skal_kun_hente_fra_klient_1_gang() {
        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode(STRENG_FORTROLIG_FNR);
        assertThat(diskresjonskode).hasValue(Diskresjonskode.STRENGT_FORTROLIG);

        Optional<Diskresjonskode> diskresjonskode2 = persondataClient.hentDiskresjonskode(STRENG_FORTROLIG_FNR);
        assertThat(diskresjonskode2).hasValue(Diskresjonskode.STRENGT_FORTROLIG);

        verify(pdlClient, times(1)).hentPersondata(any());
    }

    @Test
    void hentDiskresjonskoder_henter_bare_de_som_mangler_i_cache() {
        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode(STRENG_FORTROLIG_FNR);
        assertThat(diskresjonskode).hasValue(Diskresjonskode.STRENGT_FORTROLIG);

        Map<String, Optional<Diskresjonskode>> diskresjonskodeMap = persondataClient.hentDiskresjonskoder(
            Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR)
        );

        assertThat(diskresjonskodeMap.get(STRENG_FORTROLIG_FNR)).hasValue(Diskresjonskode.STRENGT_FORTROLIG);
        assertThat(diskresjonskodeMap.get(FORTROLIG_FNR)).hasValue(Diskresjonskode.FORTROLIG);
        assertThat(diskresjonskodeMap.get(UGRADERT_FNR)).hasValue(Diskresjonskode.UGRADERT);

        verify(pdlClient, times(1)).hentPersondata(STRENG_FORTROLIG_FNR);
        verify(pdlClient, times(1)).hentPersonBolk(Set.of(FORTROLIG_FNR, UGRADERT_FNR));
    }

    @Test
    void hentDiskresjonskode_henter_bare_dersom_det_mangler_i_cache() {
        Map<String, Optional<Diskresjonskode>> diskresjonskodeMap = persondataClient.hentDiskresjonskoder(
            Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR)
        );

        assertThat(diskresjonskodeMap.get(STRENG_FORTROLIG_FNR)).hasValue(Diskresjonskode.STRENGT_FORTROLIG);
        assertThat(diskresjonskodeMap.get(FORTROLIG_FNR)).hasValue(Diskresjonskode.FORTROLIG);
        assertThat(diskresjonskodeMap.get(UGRADERT_FNR)).hasValue(Diskresjonskode.UGRADERT);

        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode(FORTROLIG_FNR);
        assertEquals(Diskresjonskode.FORTROLIG, diskresjonskode.orElse(null));

        verify(pdlClient, never()).hentPersondata(any());
        verify(pdlClient, times(1)).hentPersonBolk(Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR));
    }

    @Test
    void hentDiskresjonskode_lagrer_ikke_i_cache_dersom_respons_fra_pdl_er_tom() {
        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode(TOM_RESPONS_FNR);
        assertThat(diskresjonskode).isEmpty();

        Optional<Diskresjonskode> diskresjonskode2 = persondataClient.hentDiskresjonskode(TOM_RESPONS_FNR);
        assertThat(diskresjonskode2).isEmpty();

        verify(pdlClient, times(2)).hentPersondata(any());
    }

    @Test
    void hentDiskresjonskoder_lagrer_ikke_i_cache_og_defaulter_til_UGRADERT_dersom_respons_fra_pdl_er_tom() {
        Map<String, Optional<Diskresjonskode>> diskresjonskodeMap = persondataClient.hentDiskresjonskoder(
            Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR, TOM_RESPONS_FNR)
        );

        assertThat(diskresjonskodeMap.get(STRENG_FORTROLIG_FNR)).hasValue(Diskresjonskode.STRENGT_FORTROLIG);
        assertThat(diskresjonskodeMap.get(FORTROLIG_FNR)).hasValue(Diskresjonskode.FORTROLIG);
        assertThat(diskresjonskodeMap.get(UGRADERT_FNR)).hasValue(Diskresjonskode.UGRADERT);
        assertThat(diskresjonskodeMap.get(TOM_RESPONS_FNR)).hasValue(Diskresjonskode.UGRADERT);

        Map<String, Diskresjonskode> diskresjonskodeMap2 = persondataClient.hentDiskresjonskoderEllerDefault(
            Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR, TOM_RESPONS_FNR),
            Diskresjonskode.UGRADERT
        );

        assertEquals(Diskresjonskode.STRENGT_FORTROLIG, diskresjonskodeMap2.get(STRENG_FORTROLIG_FNR));
        assertEquals(Diskresjonskode.FORTROLIG, diskresjonskodeMap2.get(FORTROLIG_FNR));
        assertEquals(Diskresjonskode.UGRADERT, diskresjonskodeMap2.get(UGRADERT_FNR));
        assertEquals(Diskresjonskode.UGRADERT, diskresjonskodeMap2.get(TOM_RESPONS_FNR));

        verify(pdlClient, times(1))
            .hentPersonBolk(Set.of(STRENG_FORTROLIG_FNR, FORTROLIG_FNR, UGRADERT_FNR, TOM_RESPONS_FNR));
    }

}
