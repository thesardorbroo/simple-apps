package uz.sardorbroo.musicfinderbot.enumeration;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum Language {
    // Todo. Should fix that
    UZ("en", Locale.ENGLISH),
    RU("en", Locale.ENGLISH),
    EN("en", Locale.ENGLISH);
    private final String code;
    private final Locale locale;

    Language(String code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }
}
