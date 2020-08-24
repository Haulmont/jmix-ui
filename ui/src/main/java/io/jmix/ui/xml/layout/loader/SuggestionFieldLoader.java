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

import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.ui.component.SuggestionField;
import io.jmix.ui.component.compatibility.CaptionAdapter;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class SuggestionFieldLoader extends SuggestionFieldQueryLoader<SuggestionField> {

    @Override
    public void createComponent() {
        resultComponent = factory.create(SuggestionField.NAME);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        super.loadComponent();

        loadTabIndex(resultComponent, element);

        loadAsyncSearchDelayMs(resultComponent, element);
        loadMinSearchStringLength(resultComponent, element);
        loadSuggestionsLimit(resultComponent, element);

        loadCaptionProperty(resultComponent, element);
        loadInputPrompt(resultComponent, element);

        loadPopupWidth(resultComponent, element);

        loadQuery(resultComponent, element);
    }

    protected void loadPopupWidth(SuggestionField suggestionField, Element element) {
        String popupWidth = element.attributeValue("popupWidth");
        if (StringUtils.isNotEmpty(popupWidth)) {
            suggestionField.setPopupWidth(popupWidth);
        }
    }

    protected void loadCaptionProperty(SuggestionField suggestionField, Element element) {
        String captionProperty = element.attributeValue("captionProperty");
        if (StringUtils.isNotEmpty(captionProperty)) {
            suggestionField.setOptionCaptionProvider(
                    new CaptionAdapter(captionProperty, applicationContext.getBean(Metadata.class), applicationContext.getBean(MetadataTools.class)));
        }
    }

    protected void loadSuggestionsLimit(SuggestionField suggestionField, Element element) {
        String suggestionsLimit = element.attributeValue("suggestionsLimit");
        if (StringUtils.isNotEmpty(suggestionsLimit)) {
            suggestionField.setSuggestionsLimit(Integer.parseInt(suggestionsLimit));
        }
    }

    protected void loadMinSearchStringLength(SuggestionField suggestionField, Element element) {
        String minSearchStringLength = element.attributeValue("minSearchStringLength");
        if (StringUtils.isNotEmpty(minSearchStringLength)) {
            suggestionField.setMinSearchStringLength(Integer.parseInt(minSearchStringLength));
        }
    }

    protected void loadAsyncSearchDelayMs(SuggestionField suggestionField, Element element) {
        String asyncSearchDelayMs = element.attributeValue("asyncSearchDelayMs");
        if (StringUtils.isNotEmpty(asyncSearchDelayMs)) {
            suggestionField.setAsyncSearchDelayMs(Integer.parseInt(asyncSearchDelayMs));
        }
    }
}