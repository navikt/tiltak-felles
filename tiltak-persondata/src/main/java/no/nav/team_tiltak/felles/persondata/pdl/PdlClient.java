package no.nav.team_tiltak.felles.persondata.pdl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.team_tiltak.felles.persondata.cache.PdlResponseCache;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlRequest;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponseBolk;
import no.nav.team_tiltak.felles.persondata.utils.ResourceUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class PdlClient {
    private static final Logger log = LoggerFactory.getLogger(PdlClient.class);

    private final OkHttpClient httpClient;
    private final PdlResponseCache pdlResponseCache;
    private final String baseUrl;
    private final String hentPersondataQuery;
    private final String hentPersonBolkQuery;
    private final Supplier<String> tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public PdlClient(String baseUrl, OkHttpClient httpClient, Supplier<String> tokenProvider) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.tokenProvider = tokenProvider;
        this.pdlResponseCache = new PdlResponseCache();
        this.hentPersonBolkQuery = ResourceUtil.getResourceAsString(this, "/pdl/hentPersonBolk.graphql");
        this.hentPersondataQuery = ResourceUtil.getResourceAsString(this, "/pdl/hentPersondata.graphql");
    }

    public Optional<PdlResponseBolk> hentPersonBolk(Set<String> fnr) {
        log.info("Henter {} personer i bolk fra PDL", fnr.size());
        PdlRequest<PdlRequest.BolkVariables> pdlRequest = new PdlRequest<>(
            hentPersonBolkQuery,
            new PdlRequest.BolkVariables(new ArrayList<>(fnr))
        );

        Optional<PdlResponseBolk> pdlResponseOpt = post(baseUrl, pdlRequest, PdlResponseBolk.class);
        log.info(
            pdlResponseOpt
                .filter(response -> !response.hentPersonBolk().isEmpty())
                .map(response -> "Fikk svar på " + response.hentPersonBolk().size() + " ved henting av personer i bolk")
                .orElse("Svar fra PDL var tomt ved henting av personer i bolk")
        );
        return pdlResponseOpt;
    }

    public Optional<PdlResponse> hentPersondata(String fnr) {
        Optional<PdlResponse> cached = Optional.ofNullable(pdlResponseCache.getIfPresent(fnr));
        if (cached.isPresent()) {
            log.info("Henter person fra PDL-cache");
            return cached;
        }

        log.info("Henter person fra PDL");
        PdlRequest<PdlRequest.Variables> pdlRequest = new PdlRequest<>(
            hentPersondataQuery,
            new PdlRequest.Variables(fnr)
        );

        Optional<PdlResponse> pdlResponseOpt = post(baseUrl, pdlRequest, PdlResponse.class);
        pdlResponseOpt.ifPresent(Response -> pdlResponseCache.put(fnr, Response));
        log.info(
            pdlResponseOpt
                .map(response -> "Hentet person fra PDL")
                .orElse("Svar fra PDL var tomt ved henting av person")
        );
        return pdlResponseOpt;
    }

    private <T, R> Optional<R> post(String url, PdlRequest<T> pdlRequest, Class<R> responseType) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + tokenProvider.get())
                .addHeader("Behandlingsnummer", "B662")
                .post(RequestBody.create(
                    objectMapper.writeValueAsString(pdlRequest),
                    MediaType.parse("application/json")
                ))
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.warn("Ingen respons fra PDL");
                    return Optional.empty();
                }
                return Optional.ofNullable(objectMapper.readValue(response.body().string(), responseType));
            } catch (IOException e) {
                log.error("Feil fra PDL", e);
                return Optional.empty();
            }
        } catch (JsonProcessingException e) {
            log.error("Feil ved tolking av json fra PDL", e);
            return Optional.empty();
        }
    }
}
