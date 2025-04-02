package no.nav.team_tiltak.felles.persondata.utils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtils {
    public static <K, V> Map<K, V> concat(Map<K, V> a, Map<K, V> b) {
        return Stream
            .concat(a.entrySet().stream(), b.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> mapKeys(Map<String, V> map, Function<String, K> keyMapper) {
        return map.entrySet().stream().collect(
            Collectors.toMap(entry -> keyMapper.apply(entry.getKey()), Map.Entry::getValue)
        );
    }
}
