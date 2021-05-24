/*
 * Copyright 2021 Haulmont.
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

package io.jmix.ui.app.propertyfilter.dateinterval.converter;

import io.jmix.core.annotation.Internal;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.BaseDateInterval;

import javax.annotation.Nullable;

// todo rp javaDocs
@Internal
public interface DateIntervalConverter {

    String INCLUDING_CURRENT_DESCR = "including_current";

    @Nullable
    BaseDateInterval parse(String dateInterval);

    String format(BaseDateInterval dateInterval);

    @Nullable
    String getLocalizedValue(@Nullable BaseDateInterval dateInterval);

    boolean matches(String dateInterval);

    boolean supports(BaseDateInterval.Type type);
}
