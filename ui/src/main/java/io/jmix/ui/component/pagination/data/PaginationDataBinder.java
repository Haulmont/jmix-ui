/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.ui.component.pagination.data;

import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.ui.component.PaginationComponent;
import io.jmix.ui.model.CollectionChangeType;

import java.util.function.Consumer;

/**
 * Interface defining methods for managing data loading in {@link PaginationComponent} component.
 */
public interface PaginationDataBinder {

    /**
     * @return first position to load
     */
    int getFirstResult();

    /**
     * Sets the position of the first instance to load.
     */
    void setFirstResult(int startPosition);

    /**
     * @return maximum number of instances to load
     */
    int getMaxResult();

    /**
     * Sets maximum number of instances to load.
     */
    void setMaxResults(int maxResults);

    /**
     * @return number of instances in the data store
     */
    int getCount();

    /**
     * @return number of instances are currently loaded
     */
    int size();

    /**
     * Reloads instances.
     */
    void refresh();

    /**
     * Removes collection change listener.
     */
    void removeCollectionChangeListener();

    /**
     * Sets collection change listener.
     */
    void setCollectionChangeListener(Consumer<CollectionChangeType> listener);

    /**
     * @return meta class of entity
     */
    MetaClass getEntityMetaClass();
}
