package uz.sardorbroo.musicfinderbot.util;

import org.apache.commons.lang3.StringUtils;

public class StringMaskUtils {
    private static final Integer MASKING_LENGTH = 12;
    private static final Integer MIN_LENGTH_ABBREVIATION = 4;

    public static String mask(String target) {

        if (StringUtils.isBlank(target)) {
            return "";
        }

        final int countOfMaskingSymbols = target.length() < MASKING_LENGTH ? MIN_LENGTH_ABBREVIATION : MASKING_LENGTH;
        return StringUtils.abbreviate(target, countOfMaskingSymbols);
    }
}
