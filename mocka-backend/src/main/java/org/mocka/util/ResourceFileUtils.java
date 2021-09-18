package org.mocka.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class ResourceFileUtils {

    public static InputStream open(String resourceLocation) throws IOException {
        var resource = new ClassPathResource(resourceLocation);
        if (resource.exists()) {
            return resource.getInputStream();
        } else {
            // try to load from file system
            return new FileInputStream(ResourceUtils.getFile(resourceLocation));
        }
    }
}
