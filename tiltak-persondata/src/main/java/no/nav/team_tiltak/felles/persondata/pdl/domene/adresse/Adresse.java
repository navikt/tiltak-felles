package no.nav.team_tiltak.felles.persondata.pdl.domene.adresse;

public record Adresse(
	//example = "Bostedsadresse", description = "Oppholdsadresse/Kontaktadresse/KontaktinformasjonForDødsbo/")
	AdresseKildeCode adresseKilde,
	//example = "NorskPostadresse", description = "NorskPostadresse/UtenlandskPostadresse")
	PostadresseType type,
	//example = "Postboks 5 St Olavs Plass")
	String adresselinje1,
	//example = "adresselinje 2", nullable = true)
	String adresselinje2,
	//example = "adresselinje 3", nullable = true)
	String adresselinje3,
	//example = "0130")
	String postnummer,
	//example = "OSLO")
	String poststed,
	//example = "NO")
	String landkode,
	//example = "Norge")
	String land)
{}