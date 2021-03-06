/*
 * Copyright (c) 2020 Haulmont.
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

package navigation.screen.with_type.by_interface;

import io.jmix.ui.screen.EditorScreen;
import io.jmix.ui.screen.Screen;
import test_support.entity.sec.User;

public class ScreenImplEditorScreen extends Screen implements EditorScreen<User> {

    @Override
    public void setEntityToEdit(User entity) {
    }

    @Override
    public User getEditedEntity() {
        return null;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean hasUnsavedChanges() {
        return false;
    }
}
