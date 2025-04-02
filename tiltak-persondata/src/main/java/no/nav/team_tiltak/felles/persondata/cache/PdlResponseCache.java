package no.nav.team_tiltak.felles.persondata.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;
import no.nav.team_tiltak.felles.persondata.pdl.domene.PdlResponse;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PdlResponseCache {
    private final Cache<String, PdlResponse> cache;

    public PdlResponseCache() {
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(24))
            .maximumSize(25_000)
            .build();
    }

    public PdlResponse getIfPresent(String fnr) {
        return cache.getIfPresent(fnr);
    }

    public void put(String fnr, PdlResponse pdlResponse) {
        cache.put(fnr, pdlResponse);
    }

}
