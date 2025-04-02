package no.nav.team_tiltak.felles.persondata.pdl.domene;

public enum Diskresjonskode {
    STRENGT_FORTROLIG_UTLAND,
    STRENGT_FORTROLIG,
    FORTROLIG,
    UGRADERT;

    public boolean erKode6() {
        return STRENGT_FORTROLIG.equals(this) || STRENGT_FORTROLIG_UTLAND.equals(this);
    }

    public boolean erKode7() {
        return FORTROLIG.equals(this);
    }

    public boolean erKode6Eller7() {
        return erKode6() || erKode7();
    }

    public static Diskresjonskode parse(String str) {
        return switch (str) {
            case "STRENGT_FORTROLIG_UTLAND" -> STRENGT_FORTROLIG_UTLAND;
            case "STRENGT_FORTROLIG" -> STRENGT_FORTROLIG;
            case "FORTROLIG" -> FORTROLIG;
            case "UGRADERT" -> UGRADERT;
            case null, default -> UGRADERT;
        };
    }
}
