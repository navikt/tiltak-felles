package no.nav.team_tiltak.felles.persondata.pdl.domene;

import java.util.List;

public record PdlRequest<V>(
    String query,
    V variables
) {
    public record Variables(String ident) {}
    public record BolkVariables(List<String> identer) {}
}
