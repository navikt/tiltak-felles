package no.nav.team_tiltak.felles.persondata;

import no.nav.common.rest.client.RestClient;
import no.nav.team_tiltak.felles.persondata.cache.DiskresjonskodeCache;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Navn;
import no.nav.team_tiltak.felles.persondata.pdl.PdlClient;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;
import no.nav.team_tiltak.felles.persondata.utils.MapUtils;
import okhttp3.OkHttpClient;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PersondataClient {
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

    public Diskresjonskode hentDiskresjonskode(String fnr) {
        return diskresjonskodeCache.get(fnr).orElseGet(() -> {
            Optional<Diskresjonskode> diskresjonskodeOpt = pdlClient.hentPersondata(fnr)
                .flatMap(PdlResponse::utledDiskresjonskode);
            diskresjonskodeCache.putIfPresent(fnr, diskresjonskodeOpt);
            return diskresjonskodeOpt.orElse(Diskresjonskode.UGRADERT);
        });
    }

    public Map<String, Diskresjonskode> hentDiskresjonskoder(Set<String> fnrSet) {
        return hentDiskresjonskoder(fnrSet, Function.identity());
    }

    public <T>Map<T, Diskresjonskode> hentDiskresjonskoder(Set<String> fnrSet, Function<String, T> mapper) {
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
            return MapUtils.mapKeys(diskresjonskoderFraCache, mapper);
        }

        Map<String, Optional<Diskresjonskode>> diskresjonskodeOptFraPdl = pdlClient
            .hentPersonBolk(fnrSomIkkeFinnesICache)
            .map(bolk -> bolk.utledDiskresjonskoder(fnrSomIkkeFinnesICache))
            .orElse(Collections.emptyMap());

        diskresjonskodeCache.putAllIfPresent(diskresjonskodeOptFraPdl);

        Map<String, Diskresjonskode> diskresjonskodeFraPdl = diskresjonskodeOptFraPdl.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().orElse(Diskresjonskode.UGRADERT)
            ));

        return MapUtils.mapKeys(
            MapUtils.concat(diskresjonskoderFraCache, diskresjonskodeFraPdl),
            mapper
        );
    }

    public Optional<String> hentGjeldendeAktorId(String fnr) {
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledGjeldendeIdent);
    }

    public Optional<String> hentGeografiskTilknytning(String fnr) {
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledGeoLokasjon);
    }

    public Navn hentNavn(String fnr) {
        return pdlClient.hentPersondata(fnr).flatMap(PdlResponse::utledNavn).orElse(Navn.TOMT_NAVN);
    }

}
