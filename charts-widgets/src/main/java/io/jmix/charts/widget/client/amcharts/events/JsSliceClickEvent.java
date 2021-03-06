/*
 * Copyright 2021 Haulmont.
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

package io.jmix.charts.widget.client.amcharts.events;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;

public class JsSliceClickEvent extends JavaScriptObject {

    protected JsSliceClickEvent() {
    }

    public final native String getItemKey() /*-{
        if (this.dataItem && this.dataItem.dataContext) {
            //noinspection JSUnresolvedVariable
            return this.dataItem.dataContext.$k;
        }
        return null;
    }-*/;

    public final native int getItemIndex() /*-{
        if (this.dataItem && this.dataItem.index) {
            return this.dataItem.index;
        }
        return -1;
    }-*/;

    public final native NativeEvent getMouseEvent() /*-{
        return this.event;
    }-*/;
}