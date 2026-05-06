package no.nav.team_tiltak.felles.persondata.pdl.domene.adresse;

public record Adresse(
	//eksempel = "Bostedsadresse", description = "Oppholdsadresse/Kontaktadresse/KontaktinformasjonForDødsbo/"
	AdresseKildeCode adresseKilde,
	//eksempel = "NorskPostadresse", description = "NorskPostadresse/UtenlandskPostadresse"
	PostadresseType type,
	//eksempel = "Postboks 5 St Olavs Plass"
	String adresselinje1,
	//eksempel = "adresselinje 2", nullable = true
	String adresselinje2,
	//eksempel = "adresselinje 3", nullable = true
	String adresselinje3,
	//eksempel = "0130"
	String postnummer,
	//eksempel = "OSLO"
	String poststed,
	//eksempel = "NO"
	String landkode,
	//eksempel = "Norge"
	String land)
{}