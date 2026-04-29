package no.nav.team_tiltak.felles.persondata.pdl.domene;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PdlResponseMedAdresseTest {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void deserialiserer_adressedata_og_se_at_navn_og_diskresjonskode_er_med() throws Exception {
		String json = """
			{
			  "data": {
				"hentPerson": {
				  "adressebeskyttelse": [{ "gradering": "FORTROLIG" }],
				  "doedsfall": [{ "doedsdato": "2024-01-15" }],
				  "navn": [{
					"fornavn": "Donald",
					"mellomnavn": null,
					"etternavn": "Duck",
					"forkortetNavn": "Donald Duck",
					"gyldigFraOgMed": "2020-01-01",
					"metadata": {
					  "master": "FREG",
					  "endringer": [{ "type": "OPPRETT", "registrert": "2020-01-01T10:15:30" }]
					}
				  }],
				  "kontaktadresse": [{
					"gyldigFraOgMed": "2024-01-01",
					"gyldigTilOgMed": "2024-12-31",
					"type": "Innland",
					"coAdressenavn": "c/o Skrue McDuck",
					"postboksadresse": {
					  "postbokseier": "Donald Duck",
					  "postboks": "1234",
					  "postnummer": "0123"
					},
					"vegadresse": {
					  "matrikkelId": "1",
					  "husnummer": "5",
					  "husbokstav": "A",
					  "bruksenhetsnummer": "H0101",
					  "adressenavn": "Andebyveien",
					  "kommunenummer": "0301",
					  "bydelsnummer": "01",
					  "tilleggsnavn": "Andeby",
					  "postnummer": "0123"
					},
					"metadata": {
					  "master": "PDL",
					  "historisk": false,
					  "endringer": [{
						"type": "KORRIGER",
						"registrert": "2024-01-02T00:00:00",
						"registrertAv": "Z12345",
						"systemkilde": "TPS",
						"kilde": "bruker"
					  }]
					}
				  }],
				  "oppholdsadresse": [{
					"gyldigFraOgMed": "2023-01-01",
					"gyldigTilOgMed": "2023-12-31",
					"coAdressenavn": "c/o Daisy Duck",
					"utenlandskAdresse": {
					  "adressenavnNummer": "Rue 1",
					  "bygningEtasjeLeilighet": "3",
					  "postboksNummerNavn": "Box 8",
					  "postkode": "75000",
					  "bySted": "Paris",
					  "regionDistriktOmraade": "Ile-de-France",
					  "landkode": "FR"
					},
					"vegadresse": {
					  "matrikkelId": "2",
					  "husnummer": "8",
					  "husbokstav": "B",
					  "bruksenhetsnummer": "H0202",
					  "adressenavn": "Oppholdsgata",
					  "kommunenummer": "0301",
					  "bydelsnummer": "02",
					  "tilleggsnavn": "Leilighet",
					  "postnummer": "0456"
					},
					"matrikkeladresse": {
					  "matrikkelId": "3",
					  "bruksenhetsnummer": "H0303",
					  "tilleggsnavn": "Matrikkel",
					  "postnummer": "0456",
					  "kommunenummer": "0301"
					},
					"oppholdAnnetSted": "false",
					"metadata": {
					  "master": "FREG",
					  "endringer": [{ "type": "OPPRETT", "registrert": "2023-01-01T00:00:00" }]
					}
				  }],
				  "kontaktinformasjonForDoedsbo": [{
					"skifteform": "OFFENTLIG",
					"attestutstedelsesdato": "2024-02-01",
					"personSomKontakt": {
					  "foedselsdato": "1934-06-09",
					  "personnavn": { "fornavn": "Dolly", "mellomnavn": null, "etternavn": "Duck" },
					  "identifikasjonsnummer": "12345678910"
					},
					"advokatSomKontakt": {
					  "personnavn": { "fornavn": "Ole", "mellomnavn": null, "etternavn": "Advokat" },
					  "organisasjonsnavn": "Juridisk AS",
					  "organisasjonsnummer": "999888777"
					},
					"organisasjonSomKontakt": {
					  "kontaktperson": { "fornavn": "Minni", "mellomnavn": null, "etternavn": "Mus" },
					  "organisasjonsnavn": "Kontakt AS",
					  "organisasjonsnummer": "111222333"
					},
					"adresse": {
					  "adresselinje1": "Booppgjorsveien 1",
					  "adresselinje2": "2. etasje",
					  "poststedsnavn": "Oslo",
					  "postnummer": "0123",
					  "landkode": "NO"
					},
					"metadata": { "master": "PDL" }
				  }],
				  "folkeregisteridentifikator": [{
					"identifikasjonsnummer": "01017012345",
					"type": "FNR",
					"status": "I_BRUK"
				  }],
				  "bostedsadresse": [{
					"angittFlyttedato": "2024-03-01",
					"gyldigFraOgMed": "2024-03-01",
					"gyldigTilOgMed": null,
					"coAdressenavn": null,
					"vegadresse": {
					  "matrikkelId": "4",
					  "husnummer": "10",
					  "husbokstav": null,
					  "bruksenhetsnummer": "H0404",
					  "adressenavn": "Bostedsgata",
					  "kommunenummer": "0301",
					  "bydelsnummer": "03",
					  "tilleggsnavn": null,
					  "postnummer": "0555"
					},
					"utenlandskAdresse": null,
					"matrikkeladresse": null,
					"ukjentBosted": { "bostedskommune": "0301" },
					"metadata": {
					  "master": "FREG",
					  "endringer": [{ "type": "OPPRETT", "registrert": "2024-03-01T12:00:00" }]
					}
				  }],
				  "folkeregisterpersonstatus": [{
					"status": "BOSA",
					"forenkletStatus": "bosatt",
					"folkeregistermetadata": { "kilde": "FREG" }
				  }]
				}
			  }
			}
			""";

		PdlResponseMedAdresse response = objectMapper.readValue(json, PdlResponseMedAdresse.class);

		assertThat(response.utledNavn()).hasValue(new Navn("Donald", null, "Duck"));
		assertThat(response.utledDiskresjonskode()).hasValue(Diskresjonskode.FORTROLIG);
		assertThat(response.data().hentPerson().kontaktadresse()).hasSize(1);
		assertThat(response.data().hentPerson().kontaktadresse().getFirst().metadata().historisk()).isFalse();
		assertThat(response.data().hentPerson().bostedsadresse().getFirst().ukjentBosted().bostedskommune()).isEqualTo("0301");
		assertThat(response.data().hentPerson().folkeregisterpersonstatus().getFirst().folkeregistermetadata().kilde())
			.isEqualTo("FREG");
	}

	@Test
	void resp_er_tomme_nar_data_mangler() {
		PdlResponseMedAdresse response = new PdlResponseMedAdresse(null);

		assertThat(response.utledNavn()).isEmpty();
		assertThat(response.utledDiskresjonskode()).isEmpty();
	}
}

