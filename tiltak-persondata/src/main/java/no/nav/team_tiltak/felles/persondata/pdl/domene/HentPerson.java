package no.nav.team_tiltak.felles.persondata.pdl.domene;

import java.util.List;

public record HentPerson (
    List<Adressebeskyttelse> adressebeskyttelse,
    List<Navn> navn
) {}
