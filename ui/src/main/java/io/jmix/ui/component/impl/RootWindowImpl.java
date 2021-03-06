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

package io.jmix.ui.component.impl;

import com.vaadin.server.ClientConnector;
import io.jmix.ui.component.RootWindow;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class RootWindowImpl extends WindowImpl implements RootWindow {
    public RootWindowImpl() {
        setSizeFull();

        component.addAttachListener(this::handleAttachToUI);
    }

    private void handleAttachToUI(ClientConnector.AttachEvent event) {
        String caption = getCaption();

        if (StringUtils.isNoneEmpty(caption)) {
            component.getUI().getPage().setTitle(caption);
        }
    }

    @Override
    public void setCaption(@Nullable String caption) {
        super.setCaption(caption);

        if (component.isAttached()) {
            component.getUI().getPage().setTitle(caption);
        }
    }
}