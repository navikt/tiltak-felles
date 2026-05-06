package no.nav.team_tiltak.felles.persondata.pdl.domene.adresse;

public record Adresse(
	//eksempel = "Bostedsadresse", beskrivelse = "Oppholdsadresse/Kontaktadresse/KontaktinformasjonForDødsbo/"
	AdresseKildeCode adresseKilde,
	//eksempel = "NorskPostadresse", beskrivelse = "NorskPostadresse/UtenlandskPostadresse"
	PostadresseType type,
	//eksempel = "Postboks 5 St Olavs Plass"
	String adresselinje1,
	//eksempel = "adresselinje 2"
	String adresselinje2,
	//eksempel = "adresselinje 3
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