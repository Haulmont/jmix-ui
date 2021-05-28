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

import io.jmix.ui.component.Component;
import io.jmix.ui.xml.layout.ComponentLoader;

public class CompositeComponentLoaderContext implements ComponentLoader.CompositeComponentContext {

    protected Class<? extends Component> componentClass;
    protected String descriptorPath;
    protected String messageGroup;

    @Override
    public String getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(String messageGroup) {
        this.messageGroup = messageGroup;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<? extends Component> componentClass) {
        this.componentClass = componentClass;
    }

    @Override
    public String getDescriptorPath() {
        return descriptorPath;
    }

    public void setDescriptorPath(String descriptorPath) {
        this.descriptorPath = descriptorPath;
    }
}