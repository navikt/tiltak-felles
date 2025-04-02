package no.nav.team_tiltak.felles.persondata.pdl.domene;

public record HentPersonBolk(
    String ident,
    HentPerson person,
    String code
) {
    public static final String OK = "ok";
    public boolean isOk() {
        return OK.equals(code);
    }
}
