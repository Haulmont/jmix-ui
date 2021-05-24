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
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Internal
@Component("ui_RelativeIntervalConverter")
public class RelativeIntervalConverter implements DateIntervalConverter {

    public static final Pattern RELATIVE_PATTERN =
            Pattern.compile("RELATIVE\\s+\\w+"); // todo rp write regexp

    @Nullable
    @Override
    public BaseDateInterval parse(String dateInterval) {
        return null;
    }

    @Override
    public String format(BaseDateInterval dateInterval) {
        return null;
    }

    @Nullable
    @Override
    public String getLocalizedValue(@Nullable BaseDateInterval dateInterval) {
        return null;
    }

    @Override
    public boolean matches(String dateInterval) {
        return RELATIVE_PATTERN.matcher(dateInterval).matches();
    }

    @Override
    public boolean supports(BaseDateInterval.Type type) {
        return type == BaseDateInterval.Type.RELATIVE;
    }
}
