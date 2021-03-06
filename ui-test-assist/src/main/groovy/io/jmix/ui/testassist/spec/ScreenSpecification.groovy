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

package io.jmix.ui.testassist.spec

import io.jmix.ui.Screens
import io.jmix.ui.UiProperties
import io.jmix.ui.screen.OpenMode
import io.jmix.ui.screen.Screen
import org.springframework.beans.factory.annotation.Autowired

@SuppressWarnings(["GroovyAccessibility", "GroovyAssignabilityCheck"])
class ScreenSpecification extends UiTestAssistSpecification {

    @Autowired
    UiProperties uiProperties

    @Override
    void setup() {
        exportScreensPackages(['io.jmix.ui.testassist.app.main'])
    }

    protected Screens getScreens() {
        vaadinUi.screens
    }

    protected Screen showTestMainScreen() {
        def mainScreen = screens.create("testMainScreen", OpenMode.ROOT)
        screens.show(mainScreen)
        mainScreen
    }
}
