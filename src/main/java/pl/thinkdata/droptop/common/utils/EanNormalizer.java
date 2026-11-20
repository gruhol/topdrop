package pl.thinkdata.droptop.common.utils;

import java.util.regex.Pattern;

public final class EanNormalizer {

    // Białe znaki Unicode (w tym twarda spacja U+00A0, której String.strip() nie usuwa),
    // znaki zerowej szerokości oraz BOM - mogą przyjść w XML z Platonu.
    private static final Pattern INVISIBLE_CHARS = Pattern.compile("[\\s\\p{Z}\\u200B\\u200C\\u200D\\u2060\\uFEFF]+");

    private EanNormalizer() {
    }

    public static String normalize(String ean) {
        if (ean == null) {
            return null;
        }
        return INVISIBLE_CHARS.matcher(ean).replaceAll("");
    }
}
