package no.nav.team_tiltak.felles.persondata.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import no.nav.team_tiltak.felles.persondata.pdl.domene.Diskresjonskode;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DiskresjonskodeCache {
    private final Cache<String, Diskresjonskode> cache;

    public DiskresjonskodeCache() {
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(24))
            .maximumSize(25_000)
            .build();
    }

    public Optional<Diskresjonskode> get(String fnr) {
        return Optional.ofNullable(cache.getIfPresent(fnr));
    }

    public Map<String, Diskresjonskode> getIfPresent(Set<String> fnrSet) {
        return fnrSet.stream()
            .map(fnr -> Map.entry(fnr, Optional.ofNullable(cache.getIfPresent(fnr))))
            .filter(entry -> entry.getValue().isPresent())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get()
            ));
    }

    public void putIfPresent(String fnr, Optional<Diskresjonskode> diskresjonskodeOpt) {
        diskresjonskodeOpt.ifPresent(diskresjonskode -> put(fnr, diskresjonskode));
    }

    private void put(String fnr, Diskresjonskode diskresjonskode) {
        cache.put(fnr, diskresjonskode);
    }

    public void putAllIfPresent(Map<String, Optional<Diskresjonskode>> diskresjonskodeOptMap) {
        Map<String, Diskresjonskode> diskresjonskodeMap = diskresjonskodeOptMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().isPresent())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get()
            ));

        putAll(diskresjonskodeMap);
    }

    private void putAll(Map<String, Diskresjonskode> diskresjonskodeMap) {
        if (!diskresjonskodeMap.isEmpty()) {
            cache.putAll(diskresjonskodeMap);
        }
    }

}
