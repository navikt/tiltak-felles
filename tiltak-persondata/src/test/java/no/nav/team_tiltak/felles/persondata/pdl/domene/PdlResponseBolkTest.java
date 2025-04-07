package no.nav.team_tiltak.felles.persondata.pdl.domene;


import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PdlResponseBolkTest {

    @Test
    void utleder_diskresjonskoder_fra_et_set_med_fnr_korrekt() {
        String FNR_MED_TOMT_SVAR_FRA_PDL_SOM_BETYR_UGRADERT = "00000000001";
        String FNR_MED_KODE_NOT_FOUND_FRA_PDL = "00000000002";
        String FNR_MED_STRENGT_FORTROLIG = "00000000003";
        String FNR_SOM_IKKE_BLE_HENTET = "00000000004";

        Set<String> fnrSet = Set.of(
            FNR_MED_TOMT_SVAR_FRA_PDL_SOM_BETYR_UGRADERT,
            FNR_MED_KODE_NOT_FOUND_FRA_PDL,
            FNR_MED_STRENGT_FORTROLIG,
            FNR_SOM_IKKE_BLE_HENTET
        );

        PdlResponseBolk pdlResponseBolk = new PdlResponseBolk(
            new PdlResponseBolk.Data(List.of(
                new HentPersonBolk(
                    FNR_MED_TOMT_SVAR_FRA_PDL_SOM_BETYR_UGRADERT,
                    new HentPerson(Collections.emptyList(), null),
                    HentPersonBolk.OK
                ),
                new HentPersonBolk(FNR_MED_KODE_NOT_FOUND_FRA_PDL, null, "not_found"),
                new HentPersonBolk(
                    FNR_MED_STRENGT_FORTROLIG,
                    new HentPerson(List.of(new Adressebeskyttelse(Diskresjonskode.STRENGT_FORTROLIG)), null),
                    HentPersonBolk.OK
                )
            ))
        );

        Map<String, Optional<Diskresjonskode>> diskresjonskoder = pdlResponseBolk.utledDiskresjonskoder(fnrSet);

        assertThat(diskresjonskoder.get(FNR_MED_TOMT_SVAR_FRA_PDL_SOM_BETYR_UGRADERT)).hasValue(Diskresjonskode.UGRADERT);
        assertThat(diskresjonskoder.get(FNR_MED_KODE_NOT_FOUND_FRA_PDL)).isEmpty();
        assertThat(diskresjonskoder.get(FNR_MED_STRENGT_FORTROLIG)).hasValue(Diskresjonskode.STRENGT_FORTROLIG);
        assertThat(diskresjonskoder.get(FNR_SOM_IKKE_BLE_HENTET)).isEmpty();
    }

}
