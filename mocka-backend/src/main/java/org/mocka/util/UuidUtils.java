package org.mocka.util;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidUtils {

    private static final UUID EMPTY = new UUID(0, 0);

    public static UUID emptyUuid() {
        return EMPTY;
    }
}
