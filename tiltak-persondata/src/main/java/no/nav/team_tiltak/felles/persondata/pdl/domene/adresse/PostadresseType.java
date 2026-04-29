package no.nav.team_tiltak.felles.persondata.pdl.domene.adresse;

public enum PostadresseType {

	NORSKPOSTADRESSE("NorskPostadresse"),
	UTENLANDSKPOSTADRESSE("UtenlandskPostadresse");

	private final String navn;

    PostadresseType(String navn) {
        this.navn = navn;
    }
}
