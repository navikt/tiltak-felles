package no.nav.team_tiltak.felles.persondata.pdl.domene;

public record Navn(
    String fornavn,
    String mellomnavn,
    String etternavn
) {
    public static final Navn TOMT_NAVN = new Navn("", "", "");
}
