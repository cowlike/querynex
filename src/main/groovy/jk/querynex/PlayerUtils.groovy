package jk.querynex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper functions for Player class.
 * @author dmaz (original)
 */
public final class PlayerUtils {

    private static final char[] FONT_TABLE = [
        ' ', '#', '#', '#', '#', '.', '#', '#',
        '#', 9, 10, '#', ' ', 13, '.', '.',
        '[', ']', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', '.', '<', '=', '>',
        ' ', '!', '"', '#', '$', '%', '&', '\'',
        '(', ')', '*', '+', ',', '-', '.', '/',
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', ':', ';', '<', '=', '>', '?',
        '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
        '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
        'x', 'y', 'z', '{', '|', '}', '~', '<',
        '<', '=', '>', '#', '#', '.', '#', '#',
        '#', '#', ' ', '#', ' ', '>', '.', '.',
        '[', ']', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', '.', '<', '=', '>',
        ' ', '!', '"', '#', '$', '%', '&', '\'',
        '(', ')', '*', '+', ',', '-', '.', '/',
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', ':', ';', '<', '=', '>', '?',
        '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
        '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
        'x', 'y', 'z', '{', '|', '}', '~', '<'
    ]
    private static final String[] DEC_SPANS = [
        "<span style='color:#333333'>",
        "<span style='color:#FF9900'>",
        "<span style='color:#33FF00'>",
        "<span style='color:#FFFF00'>",
        "<span style='color:#3366FF'>",
        "<span style='color:#33FFFF'>",
        "<span style='color:#FF3366'>",
        "<span style='color:#FFFFFF'>",
        "<span style='color:#999999'>",
        "<span style='color:#666666'>"
    ]

    private static String getDecSpan(String match) {
        return DEC_SPANS[(int)match.charAt(1) - (int)'0'.charAt(0)];
    }
    private static final Pattern allColors = Pattern.compile("\\^\\d|\\^x(\\p{XDigit}){3}");
    private static final Pattern decColors = Pattern.compile("\\^(\\d)");
    private static final Pattern hexColors = Pattern.compile("\\^x(\\p{XDigit})(\\p{XDigit})(\\p{XDigit})");

    /**
     * Converts xonotic color codes to HTML font color tags.
     * @param string xonotic-encoded string.
     * @return HTML-encoded string.
     */
    protected static String xonoticColorsToHtml(String string) {
        String hexDecodedString = hexColors.matcher(string).replaceAll('<span style=\'color:#$1$1$2$2$3$3\'>');
        Matcher matcher = decColors.matcher(hexDecodedString);
        StringBuffer htmlName = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(htmlName, getDecSpan(matcher.group()));
        }
        // No decimal colors found
        if (htmlName.length() == 0) {
            htmlName.append(hexDecodedString);
        }
        // Close span tag for each color match found
        matcher = allColors.matcher(string);
        while (matcher.find()) {
            htmlName.append("</span>");
        }
        return htmlName.toString();
    }

    protected static String decolorName(final String name) {
        return allColors.matcher(name).replaceAll("");
    }

    /**
     * Translate special characters into displayable text in player names.
     * Converts the given name string to bytes,
     * which correspond to positions in the font table.
     *
     * @param name
     * @return Player's name converted to ascii text
     */
    protected static String sanitizeName(final String name) {
        final StringBuilder builder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c >= '\ue000' && c <= '\ue0ff') {
                c = FONT_TABLE[c - '\ue000'];
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
