/*
 * Copyright 2020 Haulmont.
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

package model_object_screens

import io.jmix.core.JmixEntity
import io.jmix.core.SaveContext
import io.jmix.ui.component.TextField
import io.jmix.ui.screen.*
import org.springframework.beans.factory.annotation.Autowired
import test_support.entity.model_objects.CustomerObject

@UiController("test_CustomerObject.edit")
@UiDescriptor("customer-object-edit.xml")
@EditedEntityContainer("customerObjectDc")
class CustomerObjectEdit extends StandardEditor<CustomerObject> {

    @Autowired
    TextField<String> nameField

    @Install(target = Target.DATA_CONTEXT)
    private Set<JmixEntity> commitDelegate(SaveContext saveContext) {
        for (JmixEntity entity : saveContext.getEntitiesToSave()) {
            TestModelObjectsStorage.getInstance().save(entity)
        }
        return new HashSet<>(saveContext.getEntitiesToSave())
    }
}