package io.citizenjournalist.as.domain.enumeration;

/**
 * The Language enumeration.
 */
public enum Language {
    ENGLISH("en"),
    GERMAN("de"),
    FRENCH("fr"),
    ITALIAN("it"),
    SPANISH("es"),
    CATALAN("ca"),
    UKRAINIAN("uk"),
    PORTUGUESE("pt"),
    JAPANESE("ja"),
    ARABIC("ar"),
    CHINESE("zh"),
    RUSSIAN("ru");

    private final String shortName;

    Language(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
