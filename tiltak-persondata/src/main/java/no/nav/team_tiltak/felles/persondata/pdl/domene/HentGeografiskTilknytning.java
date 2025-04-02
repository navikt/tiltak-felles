package no.nav.team_tiltak.felles.persondata.pdl.domene;

public record HentGeografiskTilknytning(
   String gtKommune,
   String gtBydel,
   String gtLand,
   String regel
) {
   String getGeoTilknytning(){
       return gtBydel != null ? gtBydel : gtKommune;
    }
}
