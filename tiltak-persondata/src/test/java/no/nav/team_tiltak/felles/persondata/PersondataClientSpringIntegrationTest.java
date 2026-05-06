package no.nav.team_tiltak.felles.persondata;

import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Navn;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integrasjonstest som verifiserer at PersondataClient fungerer i et Spring Boot-miljø
 * og at kall til PDL kan utføres. HTTP-kall til PDL er mocket med MockWebServer.
 */
@SpringBootTest(classes = PersondataClientSpringIntegrationTest.SpringTestConfig.class)
class PersondataClientSpringIntegrationTest {

    static MockWebServer mockWebServer;

    static {
        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke starte MockWebServer", e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("pdl.base-url", () -> mockWebServer.url("/graphql").toString());
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Autowired
    PersondataClient persondataClient;

    @Test
    void spring_kontekst_starter_og_persondata_client_er_tilgjengelig() {
        assertThat(persondataClient).isNotNull();
    }

    @Test
    void hentDiskresjonskode_gjor_post_til_pdl_og_returnerer_diskresjonskode() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {
                  "data": {
                    "hentPerson": {
                      "adressebeskyttelse": [{"gradering": "STRENGT_FORTROLIG"}],
                      "navn": []
                    },
                    "hentIdenter": null,
                    "hentGeografiskTilknytning": null
                  }
                }
                """));

        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode("11111111111");

        assertThat(diskresjonskode).contains(Diskresjonskode.STRENGT_FORTROLIG);
        assertThat(diskresjonskode.get().erKode6()).isTrue();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/graphql");
        assertThat(request.getHeader("Authorization")).startsWith("Bearer ");
        assertThat(request.getHeader("Behandlingsnummer")).isEqualTo("B662");
    }

    @Test
    void hentNavn_gjor_post_til_pdl_og_returnerer_navn() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {
                  "data": {
                    "hentPerson": {
                      "adressebeskyttelse": [],
                      "navn": [{"fornavn": "Ola", "mellomnavn": null, "etternavn": "Nordmann"}]
                    },
                    "hentIdenter": null,
                    "hentGeografiskTilknytning": null
                  }
                }
                """));

        Optional<Navn> navn = persondataClient.hentNavn("22222222222");

        assertThat(navn).isPresent();
        assertThat(navn.get().fornavn()).isEqualTo("Ola");
        assertThat(navn.get().etternavn()).isEqualTo("Nordmann");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Authorization")).startsWith("Bearer ");
    }

    @Test
    void hentGeografiskTilknytning_gjor_post_til_pdl_og_returnerer_bydel() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {
                  "data": {
                    "hentPerson": null,
                    "hentIdenter": null,
                    "hentGeografiskTilknytning": {
                      "gtKommune": null,
                      "gtBydel": "030104",
                      "gtLand": null,
                      "regel": "1"
                    }
                  }
                }
                """));

        Optional<String> geografiskTilknytning = persondataClient.hentGeografiskTilknytning("33333333333");

        assertThat(geografiskTilknytning).contains("030104");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Authorization")).startsWith("Bearer ");
    }

    @Test
    void hentDiskresjonskode_returnerer_empty_naar_pdl_svarer_tomt() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(204));

        Optional<Diskresjonskode> diskresjonskode = persondataClient.hentDiskresjonskode("44444444444");

        assertThat(diskresjonskode).isEmpty();
    }

    @Test
    void hentDiskresjonskode_kaster_exception_naar_pdl_svarer_med_feilstatus() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(500));

        assertThatThrownBy(() -> persondataClient.hentDiskresjonskode("55555555555"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Feil ved kall til PDL: 500");
    }

    @SpringBootConfiguration
    static class SpringTestConfig {

        @Bean
        PersondataClient persondataClient(@Value("${pdl.base-url}") String pdlBaseUrl) {
            return new PersondataClient(pdlBaseUrl, () -> "test-token");
        }
    }
}
