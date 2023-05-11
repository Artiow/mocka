package org.mocka.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

@UtilityClass
public class ResourceFileUtils {

    public static InputStream open(String resourceLocationFormat, Object... args) throws IOException {
        return new FileInputStream(ResourceUtils.getFile(Formatter.format(resourceLocationFormat, args)));
    }

    public static InputStream open(String resourceLocation) throws IOException {
        return new FileInputStream(ResourceUtils.getFile(resourceLocation));
    }
}
