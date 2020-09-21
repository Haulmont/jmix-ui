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

import com.vaadin.spring.annotation.UIScope;
import io.jmix.core.common.util.Preconditions;
import io.jmix.ui.AppUI;
import io.jmix.ui.UiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@UIScope
@Component(History.NAME)
public class WebHistory implements History {

    private static final Logger log = LoggerFactory.getLogger(WebHistory.class);

    @Autowired
    protected UiProperties uiProperties;

    protected AppUI ui;

    protected int now;
    protected List<NavigationState> history;

    public WebHistory() {
        this.now = -1;
        this.history = new ArrayList<>();
    }

    @Autowired
    public void setAppUi(AppUI ui) {
        this.ui = ui;
    }

    @Override
    public void forward(NavigationState navigationState) {
        if (checkNotNativeUrlHandlingMode()) {
            return;
        }
        Preconditions.checkNotNullArgument(navigationState);

        if (navigationState.equals(getNow())) {
            return;
        }

        dropFutureEntries();

        history.add(++now, navigationState);
    }

    @Override
    public NavigationState backward() {
        if (checkNotNativeUrlHandlingMode()) {
            return NavigationState.EMPTY;
        }

        if (now > 0) {
            now--;
        }

        return history.get(now);
    }

    @Nullable
    @Override
    public NavigationState getNow() {
        if (checkNotNativeUrlHandlingMode()) {
            return NavigationState.EMPTY;
        }

        return now >= 0
                ? history.get(now)
                : null;
    }

    @Nullable
    @Override
    public NavigationState getPrevious() {
        if (checkNotNativeUrlHandlingMode()) {
            return NavigationState.EMPTY;
        }

        return now - 1 >= 0
                ? history.get(now - 1)
                : null;
    }

    @Nullable
    @Override
    public NavigationState getNext() {
        if (checkNotNativeUrlHandlingMode()) {
            return NavigationState.EMPTY;
        }

        return now + 1 < history.size()
                ? history.get(now + 1)
                : null;
    }

    @Override
    public boolean searchBackward(NavigationState navigationState) {
        if (checkNotNativeUrlHandlingMode()) {
            return false;
        }

        Preconditions.checkNotNullArgument(navigationState);

        for (int i = now - 1; i >= 0; i--) {
            if (Objects.equals(history.get(i), navigationState)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean searchForward(NavigationState navigationState) {
        if (checkNotNativeUrlHandlingMode()) {
            return false;
        }

        Preconditions.checkNotNullArgument(navigationState);

        for (int i = now + 1; i < history.size(); i++) {
            if (Objects.equals(history.get(i), navigationState)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean has(NavigationState navigationState) {
        if (checkNotNativeUrlHandlingMode()) {
            return false;
        }

        Preconditions.checkNotNullArgument(navigationState);

        return history.contains(navigationState);
    }

    @Override
    public boolean replace(NavigationState navigationState) {
        if (checkNotNativeUrlHandlingMode()) {
            return false;
        }

        Preconditions.checkNotNullArgument(navigationState);

        NavigationState currentState = history.get(now);

        if (!Objects.equals(currentState.getRoot(), navigationState.getRoot())
                || !Objects.equals(currentState.getStateMark(), navigationState.getStateMark())
                || !Objects.equals(currentState.getNestedRoute(), navigationState.getNestedRoute())) {
            log.debug("It's allowed to replace state in history when only params are changed");

            return false;
        }

        history.set(now, navigationState);

        return true;
    }

    protected void dropFutureEntries() {
        for (int i = now + 1; i < history.size(); i++) {
            //noinspection RedundantCast
            history.remove((int) i);
        }
    }

    protected boolean checkNotNativeUrlHandlingMode() {
        boolean nativeMode = UrlHandlingMode.URL_ROUTES == uiProperties.getUrlHandlingMode();

        if (!nativeMode) {
            log.debug("History bean invocations are ignored for {} URL handling mode", uiProperties.getUrlHandlingMode());
        }

        return !nativeMode;
    }
}
