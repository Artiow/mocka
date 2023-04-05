package org.mocka.domain.view;

import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
class ViewUtils {

    static <E> View extractViewAnnotation(Class<E> viewClass) {
        return Optional
            .ofNullable(viewClass.getAnnotation(View.class))
            .orElseThrow(() -> new IllegalArgumentException(viewClass.getName() + " is not view"));
    }
}
