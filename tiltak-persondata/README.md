# Tiltak Persondata

## Hvordan bruke

1. Legg til en `tiltak-persondata` som en avhengighet. Siste versjon kan finnes under [Releases](https://github.com/navikt/tiltak-felles/releases).
    ```xml
    <repositories>
        ...
        <repository>
            <id>github</id>
            <url>https://github-package-registry-mirror.gc.nav.no/cached/maven-release</url>
        </repository>
        ...
    </repositories> 

   <dependency>
        <groupId>no.nav.team-tiltak</groupId>
        <artifactId>tiltak-persondata</artifactId>
        <version>${versjon}</version>
    </dependency>
    ```
2. Ved bruk av [token-support](https://github.com/navikt/token-support) kan klienten settes opp på følgende måte (gitt at konfigurasjon for tilgang til pdl er satt opp):
    ```java
    ClientProperties clientProperties = clientConfigurationProperties.getRegistration().get("pdl-api");
    new PersondataClient(
        "{url-til-pdl}",
        () -> oAuth2AccessTokenService.getAccessToken(prop).getAccessToken()
    );
    ```
