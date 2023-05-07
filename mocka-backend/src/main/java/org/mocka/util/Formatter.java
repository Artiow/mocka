package org.mocka.util;

import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

@UtilityClass
public class Formatter {

    public static String format(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}
