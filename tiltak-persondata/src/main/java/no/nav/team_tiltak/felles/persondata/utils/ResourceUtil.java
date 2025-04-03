package no.nav.team_tiltak.felles.persondata.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceUtil {

    public static String getResourceAsString(Object o, String name) {
        try (InputStream inputStream = o.getClass().getResourceAsStream(name);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
             String resource = reader.lines().collect(Collectors.joining(System.lineSeparator()));
             return resource.replaceAll("\\s+", " ");
        } catch (IOException e) {
            throw new IllegalStateException("Feil ved henting av " + name + " fra filsystem", e);
        }
    }
}
