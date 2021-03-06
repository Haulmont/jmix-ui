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

import io.jmix.ui.action.Action;
import io.jmix.ui.component.ActionOwner;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Frame;

public class ActionOwnerAssignActionPostInitTask extends AbstractAssignActionPostInitTask {
    public ActionOwnerAssignActionPostInitTask(ActionOwner component, String actionId, Frame frame) {
        super((Component) component, actionId, frame);
    }

    @Override
    protected boolean hasOwnAction(String id) {
        return false;
    }

    @Override
    protected void addAction(Action action) {
        ActionOwner actionOwner = (ActionOwner) component;
        actionOwner.setAction(action, false);
    }
}
