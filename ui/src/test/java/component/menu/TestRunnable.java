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

package component.menu;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TestRunnable implements Runnable, ApplicationContextAware {

    public static final ThreadLocal<Boolean> launched = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> applicationContextIsSet = new ThreadLocal<>();

    @Override
    public void run() {
        launched.set(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        applicationContextIsSet.set(true);
    }
}
