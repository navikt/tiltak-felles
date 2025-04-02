package no.nav.team_tiltak.felles.persondata.pdl.domene;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public record PdlRequest<V>(
    String query,
    V variables
) {
    public record Variables(String ident) {}
    public record BolkVariables(List<String> identer) {}

    public static <V>PdlRequest<V> av(URL resource, V variables) throws IOException {
        File file = new File(resource.getFile());
        String query = FileUtils.readFileToString(file, "UTF-8");
        return new PdlRequest<>(query.replaceAll("\\s+", " "), variables);
    }
}
