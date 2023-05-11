package org.mocka.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

@UtilityClass
public class ResourceFileUtils {

    public static InputStreamReader read(String resourceLocationFormat, Object... args) throws IOException {
        try (final var stream = open(resourceLocationFormat, args)) {
            return new InputStreamReader(stream);
        }
    }

    public static InputStreamReader read(String resourceLocation) throws IOException {
        try (final var stream = open(resourceLocation)) {
            return new InputStreamReader(stream);
        }
    }

    public static InputStream open(String resourceLocationFormat, Object... args) throws IOException {
        return new FileInputStream(ResourceUtils.getFile(Formatter.format(resourceLocationFormat, args)));
    }

    public static InputStream open(String resourceLocation) throws IOException {
        return new FileInputStream(ResourceUtils.getFile(resourceLocation));
    }
}
