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
package io.jmix.ui.xml.layout.loader;

import io.jmix.ui.component.Label;
import org.apache.commons.lang3.StringUtils;

public class LabelLoader extends AbstractComponentLoader<Label> {
    @Override
    public void createComponent() {
        resultComponent = factory.create(Label.NAME);
        loadId(resultComponent, element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadComponent() {
        assignXmlDescriptor(resultComponent, element);

        loadData(resultComponent, element);

        loadVisible(resultComponent, element);
        loadAlign(resultComponent, element);
        loadStyleName(resultComponent, element);

        loadHtmlSanitizerEnabled(resultComponent, element);

        String htmlEnabled = element.attributeValue("htmlEnabled");
        if (StringUtils.isNotEmpty(htmlEnabled)) {
            resultComponent.setHtmlEnabled(Boolean.parseBoolean(htmlEnabled));
        }

        String value = element.attributeValue("value");
        if (StringUtils.isNotEmpty(value)) {
            value = loadResourceString(value);
            resultComponent.setValue(value);
        }
        
        loadDescription(resultComponent, element);
        loadContextHelp(resultComponent, element);

        loadIcon(resultComponent, element);

        loadWidth(resultComponent, element);
        loadHeight(resultComponent, element);

        loadResponsive(resultComponent, element);
        loadCss(resultComponent, element);

        loadFormatter(resultComponent, element);
    }
}
