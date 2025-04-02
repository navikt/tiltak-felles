package no.nav.team_tiltak.felles.persondata.pdl.domene;

public record Identer(
    String ident,
    String gruppe,
    boolean historisk
) {}
