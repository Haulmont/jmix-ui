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

package io.jmix.ui.screen;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.jmix.ui.component.Window.SELECT_ACTION_ID;

/**
 * Interface for lookup screen controllers.
 *
 * @param <E> type of entity
 */
public interface LookupScreen<E> {
    String LOOKUP_SELECT_ACTION_ID = "lookupSelectAction";
    String LOOKUP_CANCEL_ACTION_ID = "lookupCancelAction";
    String LOOKUP_ITEM_CLICK_ACTION_ID = "lookupItemClickAction";
    String LOOKUP_ENTER_PRESSED_ACTION_ID = "lookupEnterPressed";

    /**
     * A {@link CloseAction} used when the screen is closed after the user selected an item in the lookup component.
     */
    CloseAction LOOKUP_SELECT_CLOSE_ACTION = new StandardCloseAction(SELECT_ACTION_ID);

    /**
     * @return selection handler
     */
    @Nullable
    Consumer<Collection<E>> getSelectHandler();

    /**
     * Sets selection handler for screen.
     *
     * @param selectHandler selection handler
     */
    void setSelectHandler(@Nullable Consumer<Collection<E>> selectHandler);

    /**
     * @return selection validator
     */
    Predicate<ValidationContext<E>> getSelectValidator();

    /**
     * Sets selection validator.
     *
     * @param selectValidator selection validator
     */
    void setSelectValidator(Predicate<ValidationContext<E>> selectValidator);

    /**
     * Validation data context.
     *
     * @param <T> type of entity
     */
    class ValidationContext<T> {
        private Screen screen;
        private Collection<T> selectedItems;

        public ValidationContext(Screen screen, Collection<T> selectedItems) {
            this.screen = screen;
            this.selectedItems = selectedItems;
        }

        public Screen getScreen() {
            return screen;
        }

        public Collection<T> getSelectedItems() {
            return selectedItems;
        }
    }
}
