package no.nav.team_tiltak.felles.persondata.pdl.domene.adresse;

public record Adresse(
	AdresseKildeCode adresseKilde,
	PostadresseType type,
	String adresselinje1,
	String adresselinje2,
	String adresselinje3,
	String postnummer,
	String poststed,
	String landkode,
	String land)
{}