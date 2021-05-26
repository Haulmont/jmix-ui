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

package io.jmix.ui.app.propertyfilter.dateinterval;

import com.google.common.base.Strings;
import io.jmix.core.Messages;
import io.jmix.core.annotation.Internal;
import io.jmix.ui.app.propertyfilter.dateinterval.converter.DateIntervalConverter;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.BaseDateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.DateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.predefined.PredefinedDateInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Utility class for date intervals.
 *
 * @see PredefinedDateInterval
 * @see DateInterval
 */
@Internal
@Component("ui_DateIntervalUtils")
public class DateIntervalUtils {

    protected Messages messages;
    protected List<DateIntervalConverter> dateIntervalConverters;

    @Autowired
    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    @Autowired
    public void setDateIntervalConverters(List<DateIntervalConverter> dateIntervalConverters) {
        this.dateIntervalConverters = dateIntervalConverters;
    }

    /**
     * Parses string presentation of date interval to {@link BaseDateInterval}.
     *
     * @param dateInterval string presentation of date interval
     * @return configured date interval or {@code null} if input parameter is null or empty.
     * @see DateInterval
     * @see PredefinedDateInterval
     */
    @Nullable
    public BaseDateInterval parseDateInterval(String dateInterval) {
        if (Strings.isNullOrEmpty(dateInterval)) {
            return null;
        }

        for (DateIntervalConverter converter : dateIntervalConverters) {
            if (converter.matches(dateInterval)) {
                return converter.parse(dateInterval);
            }
        }

        throw new IllegalArgumentException("Wrong date interval string format");
    }

    /**
     * Formats date interval to string presentation.
     *
     * @param dateInterval date interval instance
     * @return raw presentation of date interval
     * @see DateInterval
     * @see PredefinedDateInterval
     */
    public String formatDateInterval(BaseDateInterval dateInterval) {
        for (DateIntervalConverter converter : dateIntervalConverters) {
            if (converter.supports(dateInterval.getType())) {
                return converter.format(dateInterval);
            }
        }

        throw new IllegalStateException(String.format("Unknown date interval type: %s", dateInterval.getType()));
    }

    /**
     * Formats date interval and gets localized value.
     *
     * @param dateInterval date interval instance
     * @return localized value
     * @see DateInterval
     * @see PredefinedDateInterval
     */
    @Nullable
    public String getLocalizedValue(@Nullable BaseDateInterval dateInterval) {
        if (dateInterval == null) {
            return null;
        }

        for (DateIntervalConverter converter : dateIntervalConverters) {
            if (converter.supports(dateInterval.getType())) {
                return converter.getLocalizedValue(dateInterval);
            }
        }

        throw new IllegalStateException(String.format("Unknown date interval type: %s", dateInterval.getType()));
    }
}
