package no.nav.team_tiltak.felles.persondata;

import no.nav.common.rest.client.RestClient;
import no.nav.team_tiltak.felles.persondata.cache.DiskresjonskodeCache;
import no.nav.team_tiltak.felles.persondata.pdl.PdlClient;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Navn;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;
import no.nav.team_tiltak.felles.persondata.utils.MapUtils;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PersondataClient {
    private static final Logger log = LoggerFactory.getLogger(PersondataClient.class);

    private final PdlClient pdlClient;
    private final DiskresjonskodeCache diskresjonskodeCache;

    public PersondataClient(
        String baseUrl,
        Supplier<String> tokenProvider
    ) {
        this(baseUrl, tokenProvider, RestClient.baseClient());
    }

    public PersondataClient(
        String baseUrl,
        Supplier<String> tokenProvider,
        OkHttpClient httpClient
    ) {
        this(new PdlClient(baseUrl, httpClient, tokenProvider));
    }

    public PersondataClient(
        PdlClient pdlClient
    ) {
        this.pdlClient = pdlClient;
        this.diskresjonskodeCache = new DiskresjonskodeCache();
    }

    public Optional<Diskresjonskode> hentDiskresjonskode(String fnr) {
        log.info("Henter diskresjonskode fra PDL");

        Optional<Diskresjonskode> diskresjonskodeCacheOpt = diskresjonskodeCache.get(fnr);

        if (diskresjonskodeCacheOpt.isPresent()) {
            log.info("Fant diskresjonskode fra PDL i cache");
            return diskresjonskodeCacheOpt;
        }

        Optional<PdlResponse> pdlResponseOpt = pdlClient.hentPersondata(fnr);
        if (pdlResponseOpt.isEmpty()) {
            return Optional.empty();
        }

        Diskresjonskode diskresjonskode = pdlResponseOpt
            .flatMap(PdlResponse::utledDiskresjonskode)
            .orElse(Diskresjonskode.UGRADERT);

        diskresjonskodeCache.put(fnr, diskresjonskode);
        return Optional.of(diskresjonskode);
    }

    public Map<String, Diskresjonskode> hentDiskresjonskoderEllerDefault(
        Set<String> fnrSet,
        Diskresjonskode defaultDiskresjonskode
    ) {
        return MapUtils.defaultMap(hentDiskresjonskoder(fnrSet, Function.identity()), defaultDiskresjonskode);
    }

    public Map<String, Optional<Diskresjonskode>> hentDiskresjonskoder(Set<String> fnrSet) {
        return hentDiskresjonskoder(fnrSet, Function.identity());
    }

    public <T>Map<T, Diskresjonskode> hentDiskresjonskoderEllerDefault(
        Set<String> fnrSet,
        Function<String, T> mapper,
        Diskresjonskode defaultDiskresjonskode
    ) {
        return MapUtils.defaultMap(hentDiskresjonskoder(fnrSet, mapper), defaultDiskresjonskode);
    }

    public <T>Map<T, Optional<Diskresjonskode>> hentDiskresjonskoder(Set<String> fnrSet, Function<String, T> mapper) {
        log.info("Henter {} diskresjonskoder fra PDL", fnrSet.size());

        if (fnrSet.isEmpty()) {
            return Collections.emptyMap();
        }
        if (fnrSet.size() > 1000) {
            throw new IllegalArgumentException("Kan ikke hente diskresjonkode for mer enn 1000 om gangen");
        }

        Map<String, Diskresjonskode> diskresjonskoderFraCache = diskresjonskodeCache.getIfPresent(fnrSet);
        Set<String> fnrSomIkkeFinnesICache = fnrSet.stream()
            .filter(fnr -> !diskresjonskoderFraCache.containsKey(fnr))
            .collect(Collectors.toSet());


        if (fnrSomIkkeFinnesICache.isEmpty()) {
            log.info(
                "Fant {} diskresjonskoder fra PDL i cache",
                diskresjonskoderFraCache.size()
            );
            return MapUtils.mapKeys(MapUtils.optionalMap(diskresjonskoderFraCache), mapper);
        }

        Map<String, Optional<Diskresjonskode>> diskresjonskodeOptFraPdl = pdlClient
            .hentPersonBolk(fnrSomIkkeFinnesICache)
            .map(bolk -> bolk.utledDiskresjonskoder(fnrSomIkkeFinnesICache))
            .orElse(Collections.emptyMap());

        diskresjonskodeCache.putAllIfPresent(diskresjonskodeOptFraPdl);

        Map<String, Optional<Diskresjonskode>> diskresjonskoderOptFraPdl = diskresjonskodeOptFraPdl.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));

        if (diskresjonskoderFraCache.isEmpty()) {
            log.info(
                "{} diskresjonskoder hentet fra PDL",
                diskresjonskoderOptFraPdl.size()
            );
        } else {
            log.info(
                "Fant {} diskresjonskoder i cache og {} ble hentet fra PDL",
                diskresjonskoderFraCache.size(),
                diskresjonskoderOptFraPdl.size()
            );
        }

        return MapUtils.mapKeys(
            MapUtils.concat(MapUtils.optionalMap(diskresjonskoderFraCache), diskresjonskoderOptFraPdl),
            mapper
        );
    }

    public Optional<String> hentGjeldendeAktorId(String fnr) {
        log.info("Henter aktør-id fra PDL");
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledGjeldendeIdent);
    }

    public Optional<String> hentGeografiskTilknytning(String fnr) {
        log.info("Henter geografisk tilknytning fra PDL");
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledGeoLokasjon);
    }

    public Optional<Navn> hentNavn(String fnr) {
        log.info("Henter navn fra PDL");
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledNavn);
    }

}
