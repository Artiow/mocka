package org.mocka.domain.view;

import java.util.List;
import org.springframework.data.mongodb.core.query.Query;

public interface ViewCollection<E> {

    List<E> find(Query query);
}
