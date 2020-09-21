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

package io.jmix.ui.navigation;

import javax.annotation.Nullable;

/**
 * Interface defining methods to store and access local copy of opened screens history.
 * <p>
 * It is mainly used by UrlChangeHandler to distinguish history and navigation transitions.
 * <p>
 * <b>Pay attention that manual history mutation can lead to errors.</b>
 */
public interface History {

    String NAME = "ui_History";

    /**
     * Adds new history entry. Flushes all entries coming after current entry.
     *
     * @param navigationState new history entry
     */
    void forward(NavigationState navigationState);

    /**
     * Performs "Back" transition through history.
     *
     * @return previous history entry
     */
    NavigationState backward();

    /**
     * @return current history entry
     */
    @Nullable
    NavigationState getNow();

    /**
     * @return previous history entry
     */
    @Nullable
    NavigationState getPrevious();

    /**
     * @return next history entry
     */
    @Nullable
    NavigationState getNext();

    /**
     * Performs search for the given history entry in the past.
     *
     * @param navigationState history entry
     * @return true if entry is found, false otherwise
     */
    boolean searchBackward(NavigationState navigationState);

    /**
     * Performs search for the given history entry in the future.
     *
     * @param navigationState history entry
     * @return true if entry is found, false otherwise
     */
    boolean searchForward(NavigationState navigationState);

    /**
     * Checks whether history has the given entry.
     *
     * @param navigationState history entry
     * @return true if history has an entry, false otherwise
     */
    boolean has(NavigationState navigationState);

    /**
     * Replaces current state by the new one.
     * <p>
     * This operation is allowed when only params are changed.
     *
     * @param navigationState new state
     * @return true if old state is replaced, false otherwise
     */
    boolean replace(NavigationState navigationState);
}
