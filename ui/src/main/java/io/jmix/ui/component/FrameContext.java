/*
 * Copyright 2019 Haulmont.
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
package io.jmix.ui.component;

import io.jmix.core.common.event.Subscription;
import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Provides access to frame parameters and component values.
 */
public interface FrameContext {
    Frame getFrame();

    /**
     * For a window contains parameters passed to the window on opening.
     * <br> For a frame, linked to the window in XML, contains owning window parameters.
     * <br> For a frame, opened dynamically by {@code openFrame()} method contains parameters, passed to the method.
     */
    Map<String, Object> getParams();

    /**
     * Value of an external parameters passed to the window on opening.
     */
    <T> T getParamValue(String param);

    /**
     * Retrieves value of a component by complex name, dereferencing path to the component and possible drill down to the value.
     *
     * @param property path to the value. Parsed by the following rules:
     *                 <br>First split by dots taking into account square brackets, and looking for a component from left to right.
     *                 <br>If a component not found, return null.
     *                 <br>If a component found and it is a {@link HasValue}
     *                 or {@link ListComponent}, retrieve its value.
     *                 <br>If the value is null, return it.
     *                 <br>If there is nothing left in the path after the component name, return the value.
     *                 <br>Else if the value is entity, drill down to it and return the value
     *                 of the property by remaining property path.
     *                 <br>If the value is an {@link EnumClass} and remaining
     *                 property path is "id", return EnumClass.getId() value.
     */
    @Nullable
    <T> T getValue(String property);

    /**
     * Set value of a component by its path in the window
     *
     * @param property path to the component (separated by dots, taking into account square brackets)
     * @param value    value to set
     * @throws UnsupportedOperationException if the component not found or is not a {@link HasValue}
     */
    void setValue(String property, @Nullable Object value);

    /**
     * Add the value listener to the specified component
     *
     * @param componentName a component name
     * @param listener      a listener to add
     */
    Subscription addValueChangeListener(String componentName, Consumer<HasValue.ValueChangeEvent> listener);
}
