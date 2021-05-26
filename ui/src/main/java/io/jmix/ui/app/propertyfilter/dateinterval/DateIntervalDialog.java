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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.jmix.core.Messages;
import io.jmix.ui.Notifications;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.BaseDateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.BaseDateInterval.Type;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.DateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.RelativeDateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.predefined.PredefinedDateInterval;
import io.jmix.ui.app.propertyfilter.dateinterval.interval.predefined.PredefinedDateIntervalRegistry;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date interval editor.
 */
@UiController("ui_DateIntervalDialog")
@UiDescriptor("date-interval-dialog.xml")
public class DateIntervalDialog extends Screen {

    @Autowired
    protected Messages messages;
    @Autowired
    protected Notifications notifications;
    @Autowired
    protected PredefinedDateIntervalRegistry intervalFactory;

    @Autowired(required = false)
    protected RelativeDateTimeMomentProvider relativeMomentProvider;

    @Autowired
    protected RadioButtonGroup<Type> typeRadioButtonGroup;

    @Autowired
    protected TextField<Integer> numberField;
    @Autowired
    protected ComboBox<DateInterval.TimeUnit> timeUnitComboBox;
    @Autowired
    protected CheckBox includingCurrentCheckBox;

    @Autowired
    protected ComboBox<PredefinedDateInterval> predefinedIntervalsComboBox;

    @Autowired
    protected ComboBox<RelativeDateInterval.Operation> relativeDateTimeOperationComboBox;
    @Autowired
    protected ComboBox<Enum> relativeDateTimeComboBox;

    protected Multimap<Type, Field> componentVisibilityMap = ArrayListMultimap.create();

    protected BaseDateInterval value;

    @Subscribe
    protected void onInit(InitEvent event) {
        initTypeRadioButtonGroup();
        initTimeUnitComboBox();
        initPredefinedIntervalsComboBox();
        initRelativeDateTimeOperationComboBox();
        initRelativeDateTimeComboBox();

        componentVisibilityMap.putAll(Type.LAST,
                Arrays.asList(numberField, timeUnitComboBox, includingCurrentCheckBox));
        componentVisibilityMap.putAll(Type.NEXT,
                Arrays.asList(numberField, timeUnitComboBox, includingCurrentCheckBox));
        componentVisibilityMap.putAll(Type.RELATIVE,
                Arrays.asList(relativeDateTimeOperationComboBox, relativeDateTimeComboBox));
        componentVisibilityMap.put(Type.PREDEFINED, predefinedIntervalsComboBox);

        typeRadioButtonGroup.setValue(Type.LAST);
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if (value != null) {
            typeRadioButtonGroup.setValue(value.getType());
            if (value.getType() == Type.PREDEFINED) {
                predefinedIntervalsComboBox.setValue((PredefinedDateInterval) value);
            } else if (value.getType() == Type.RELATIVE) {
                RelativeDateInterval dateInterval = (RelativeDateInterval) value;
                Enum relativeDateTime = relativeMomentProvider.getByName(
                        dateInterval.getRelativeDateTimeMomentName());

                relativeDateTimeComboBox.setValue(relativeDateTime);
                relativeDateTimeOperationComboBox.setValue(dateInterval.getOperation());
            } else {
                DateInterval dateInterval = (DateInterval) value;
                numberField.setValue(dateInterval.getNumber());
                timeUnitComboBox.setValue(dateInterval.getTimeUnit());
                includingCurrentCheckBox.setValue(dateInterval.getIncludingCurrent());
            }
        }
    }

    /**
     * @return date value or {@code null} if value is not set
     */
    @Nullable
    public BaseDateInterval getValue() {
        return value;
    }

    /**
     * Sets value that will be applied when {@link BeforeShowEvent} is fired.
     *
     * @param value date interval
     */
    public void setValue(@Nullable BaseDateInterval value) {
        this.value = value;
    }

    /**
     * Sets value that will be applied when {@link BeforeShowEvent} is fired.
     *
     * @param value date interval
     * @return screen instance
     */
    public DateIntervalDialog withValue(@Nullable BaseDateInterval value) {
        this.value = value;
        return this;
    }

    protected void initTypeRadioButtonGroup() {
        Map<String, Type> map = relativeMomentProvider == null
                ? getLocalizedEnumMap(Arrays.asList(Type.LAST, Type.NEXT, Type.PREDEFINED))
                : getLocalizedEnumMap(Type.class);

        typeRadioButtonGroup.setOptionsMap(map);
    }

    protected void initTimeUnitComboBox() {
        Map<String, DateInterval.TimeUnit> map = getLocalizedEnumMap(DateInterval.TimeUnit.class);
        timeUnitComboBox.setOptionsMap(map);
    }

    protected void initPredefinedIntervalsComboBox() {
        List<PredefinedDateInterval> predefinedDateIntervals = intervalFactory.getAllPredefineIntervals();
        Map<String, PredefinedDateInterval> map = new LinkedHashMap<>(predefinedDateIntervals.size());

        for (PredefinedDateInterval interval : predefinedDateIntervals) {
            map.put(interval.getLocalizedCaption(), interval);
        }

        predefinedIntervalsComboBox.setOptionsMap(map);
    }

    protected void initRelativeDateTimeOperationComboBox() {
        RelativeDateInterval.Operation[] operations = RelativeDateInterval.Operation.values();
        Map<String, RelativeDateInterval.Operation> operationsMap = new LinkedHashMap<>(operations.length);

        for (RelativeDateInterval.Operation operation : operations) {
            operationsMap.put(operation.getValue(), operation);
        }
        relativeDateTimeOperationComboBox.setOptionsMap(operationsMap);
    }

    protected void initRelativeDateTimeComboBox() {
        if (relativeMomentProvider != null) {
            List<Enum> relativeDateTimeMoments = relativeMomentProvider.getAllRelativeDateTimeMoments();
            relativeDateTimeComboBox.setOptionsMap(getLocalizedEnumMap(relativeDateTimeMoments));
        }
    }

    @SuppressWarnings("rawtypes")
    protected <T extends Enum> Map<String, T> getLocalizedEnumMap(Class<T> enumClass) {
        return getLocalizedEnumMap(Arrays.asList(enumClass.getEnumConstants()));
    }

    @SuppressWarnings("rawtypes")
    protected <T extends Enum> Map<String, T> getLocalizedEnumMap(List<T> values) {
        Map<String, T> map = new LinkedHashMap<>();
        for (T enumConst : values) {
            map.put(messages.getMessage(enumConst), enumConst);
        }
        return map;
    }

    @Subscribe("typeRadioButtonGroup")
    protected void onTypeRadioButtonGroupValueChange(HasValue.ValueChangeEvent<Type> event) {
        if (event.getValue() != null) {
            componentVisibilityMap.values().forEach(component -> component.setVisible(false));
            componentVisibilityMap.get(event.getValue()).forEach(component -> component.setVisible(true));
        }
    }

    @Subscribe("okBtn")
    protected void onOkBtnClick(Button.ClickEvent event) {
        Type type = typeRadioButtonGroup.getValue();

        ValidationErrors errors = validateFields(componentVisibilityMap.get(type).toArray(new Field[0]));
        if (errors.isEmpty()) {
            if (type == Type.PREDEFINED) {
                value = predefinedIntervalsComboBox.getValue();
            } else if (type == Type.RELATIVE) {
                //noinspection ConstantConditions
                value = new RelativeDateInterval(
                        relativeDateTimeOperationComboBox.getValue(),
                        relativeDateTimeComboBox.getValue().name());
            } else {
                value = new DateInterval(type,
                        numberField.getValue(),
                        timeUnitComboBox.getValue(),
                        includingCurrentCheckBox.getValue());
            }
            close(StandardOutcome.COMMIT);
        } else {
            notifications.create(Notifications.NotificationType.TRAY)
                    .withCaption(messages.getMessage("validationFail.caption"))
                    .withDescription(
                            errors.getAll().stream()
                                    .map(item -> item.description)
                                    .collect(Collectors.joining("\n")))
                    .show();
        }
    }

    @Subscribe("cancelBtn")
    protected void onCancelBtnClick(Button.ClickEvent event) {
        close(StandardOutcome.CLOSE);
    }

    protected ValidationErrors validateFields(Field... components) {
        ValidationErrors errors = new ValidationErrors();
        for (Field field : components) {
            try {
                field.validate();
            } catch (ValidationException e) {
                errors.add(field, e.getLocalizedMessage());
            }

        }
        return errors;
    }
}

