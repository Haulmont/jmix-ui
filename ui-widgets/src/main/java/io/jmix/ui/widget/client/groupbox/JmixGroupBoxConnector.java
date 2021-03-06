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

package io.jmix.ui.widget.client.groupbox;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import io.jmix.ui.widget.JmixGroupBox;
import io.jmix.ui.widget.client.Tools;
import com.vaadin.client.*;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VPanel;
import com.vaadin.client.ui.panel.PanelConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MarginInfo;

@Connect(JmixGroupBox.class)
public class JmixGroupBoxConnector extends PanelConnector {

    public static final String CONTEXT_HELP_CLASSNAME = "jmix-context-help-button";
    public static final String CONTEXT_HELP_CLICKABLE_CLASSNAME = "jmix-context-help-button-clickable";
    public static final String REQUIRED_INDICATOR_CLASSNAME = "v-required-field-indicator";

    protected boolean widgetInitialized = false;

    @Override
    public JmixGroupBoxWidget getWidget() {
        return (JmixGroupBoxWidget) super.getWidget();
    }

    @Override
    public void init() {
        super.init();

        getWidget().expandHandler = expanded ->
                getRpcProxy(JmixGroupBoxServerRpc.class).expandStateChanged(expanded);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        if (!getState().showAsPanel && widgetInitialized) {
            LayoutManager layoutManager = getLayoutManager();
            JmixGroupBoxWidget widget = getWidget();

            layoutManager.unregisterDependency(this, widget.captionStartDeco);
            layoutManager.unregisterDependency(this, widget.captionEndDeco);
            layoutManager.unregisterDependency(this, widget.captionTextNode);
        }
    }

    @Override
    public JmixGroupBoxState getState() {
        return (JmixGroupBoxState) super.getState();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        if (!getState().showAsPanel) {
            // replace VPanel class names
            JmixGroupBoxWidget widget = getWidget();

            Tools.replaceClassNames(widget.captionNode, VPanel.CLASSNAME, widget.getStylePrimaryName());
            Tools.replaceClassNames(widget.captionWrap, VPanel.CLASSNAME, widget.getStylePrimaryName());
            Tools.replaceClassNames(widget.contentNode, VPanel.CLASSNAME, widget.getStylePrimaryName());
            Tools.replaceClassNames(widget.bottomDecoration, VPanel.CLASSNAME, widget.getStylePrimaryName());
            Tools.replaceClassNames(widget.getElement(), VPanel.CLASSNAME, widget.getStylePrimaryName());
        }
    }

    @Override
    public void layout() {
        if (!getState().showAsPanel) {
            layoutGroupBox();
        } else {
            super.layout();
        }
    }

    protected void layoutGroupBox() {
        JmixGroupBoxWidget panel = getWidget();
        LayoutManager layoutManager = getLayoutManager();

        if (isBordersVisible()) {
            int captionWidth = layoutManager.getOuterWidth(panel.captionNode);
            int captionStartWidth = layoutManager.getInnerWidth(panel.captionStartDeco);
            int totalMargin = captionWidth + captionStartWidth;

            panel.captionNode.getStyle().setWidth(captionWidth, Unit.PX);
            panel.captionWrap.getStyle().setPaddingLeft(totalMargin, Unit.PX);
            panel.captionStartDeco.getStyle().setMarginLeft(0 - totalMargin, Unit.PX);
        }

        Profiler.enter("JmixGroupBoxConnector.layout getHeights");
        // Haulmont API get max height of caption components
        int top = layoutManager.getOuterHeight(panel.captionNode);
        top = Math.max(layoutManager.getOuterHeight(panel.captionStartDeco), top);
        top = Math.max(layoutManager.getOuterHeight(panel.captionEndDeco), top);

        int bottom = layoutManager.getInnerHeight(panel.bottomDecoration);
        Profiler.leave("PanelConnector.layout getHeights");

        Style style = panel.getElement().getStyle();
        int paddingTop = 0;
        int paddingBottom = 0;
        if (panel.hasAnyOuterMargin()) {
            Profiler.enter("PanelConnector.layout get values from styles");
            // Clear previously set values

            style.clearPaddingTop();
            style.clearPaddingBottom();
            // Calculate padding from styles
            ComputedStyle computedStyle = new ComputedStyle(panel.getElement());
            paddingTop = computedStyle.getIntProperty("padding-top");
            paddingBottom = computedStyle.getIntProperty("padding-bottom");
            Profiler.leave("PanelConnector.layout get values from styles");
        }

        Profiler.enter("PanelConnector.layout modify style");
        panel.captionWrap.getStyle().setMarginTop(-top, Style.Unit.PX);
        panel.bottomDecoration.getStyle().setMarginBottom(-bottom, Style.Unit.PX);
        style.setPaddingTop(top + paddingTop, Style.Unit.PX);
        style.setPaddingBottom(bottom + paddingBottom, Style.Unit.PX);
        Profiler.leave("PanelConnector.layout modify style");

        // Update scroll positions
        Profiler.enter("PanelConnector.layout update scroll positions");
        panel.contentNode.setScrollTop(panel.scrollTop);
        panel.contentNode.setScrollLeft(panel.scrollLeft);
        Profiler.leave("PanelConnector.layout update scroll positions");

        // Read actual value back to ensure update logic is correct
        Profiler.enter("PanelConnector.layout read scroll positions");
        panel.scrollTop = panel.contentNode.getScrollTop();
        panel.scrollLeft = panel.contentNode.getScrollLeft();
        Profiler.leave("PanelConnector.layout read scroll positions");
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        JmixGroupBoxWidget widget = getWidget();

        if (!widgetInitialized) {
            widget.init();
            if (!getState().showAsPanel) {
                LayoutManager layoutManager = getLayoutManager();
                layoutManager.registerDependency(this, widget.captionStartDeco);
                layoutManager.registerDependency(this, widget.captionEndDeco);
                layoutManager.registerDependency(this, widget.captionTextNode);
            }

            widgetInitialized = true;
        }

        widget.setCollapsable(getState().collapsable);
        widget.setExpanded(getState().expanded);
        widget.setShowAsPanel(getState().showAsPanel);
        if (!getState().showAsPanel) {
            widget.setOuterMargin(new MarginInfo(getState().outerMarginsBitmask));
        }

        if (stateChangeEvent.hasPropertyChanged("caption")) {
            updateCaptionNodeWidth(widget);
        }

        if (isContextHelpIconEnabled(getState())) {
            if (getWidget().contextHelpIcon == null) {
                getWidget().contextHelpIcon = DOM.createSpan();
                getWidget().contextHelpIcon.setInnerHTML("?");
                getWidget().contextHelpIcon.setClassName(CONTEXT_HELP_CLASSNAME);

                if (hasContextHelpIconListeners(getState())) {
                    getWidget().contextHelpIcon.addClassName(CONTEXT_HELP_CLICKABLE_CLASSNAME);
                }

                Roles.getTextboxRole().setAriaHiddenState(getWidget().contextHelpIcon, true);

                getWidget().captionNode.appendChild(getWidget().contextHelpIcon);
                DOM.sinkEvents(getWidget().contextHelpIcon, VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);

                getWidget().contextHelpClickHandler = this::contextHelpIconClick;
            } else {
                getWidget().contextHelpIcon.getStyle().clearDisplay();

                updateCaptionNodeWidth(widget);
            }
        } else if (getWidget().contextHelpIcon != null) {
            getWidget().contextHelpIcon.getStyle().setDisplay(Style.Display.NONE);

            updateCaptionNodeWidth(widget);
        }

        if (getState().requiredIndicatorVisible) {
            if (getWidget().requiredIcon == null) {
                getWidget().requiredIcon = DOM.createSpan();
                getWidget().requiredIcon.setInnerHTML("*");
                getWidget().requiredIcon.setClassName(REQUIRED_INDICATOR_CLASSNAME);

                // The star should not be read by the screen reader, as it is
                // purely visual. Required state is set at the element level for
                // the screen reader.
                Roles.getTextboxRole().setAriaHiddenState(getWidget().requiredIcon, true);

                getWidget().captionNode.appendChild(getWidget().requiredIcon);
                DOM.sinkEvents(getWidget().requiredIcon, VTooltip.TOOLTIP_EVENTS);
            } else {
                getWidget().requiredIcon.getStyle().clearDisplay();

                updateCaptionNodeWidth(widget);
            }
        } else if (getWidget().requiredIcon != null) {
            getWidget().requiredIcon.getStyle().setDisplay(Style.Display.NONE);

            updateCaptionNodeWidth(widget);
        }
    }

    protected void updateCaptionNodeWidth(JmixGroupBoxWidget widget) {
        widget.captionNode.getStyle().clearWidth();
        getLayoutManager().setNeedsMeasure(this);
    }

    protected boolean isBordersVisible() {
        JmixGroupBoxWidget panel = getWidget();
        return panel.captionStartDeco.getOffsetWidth() > 0 || panel.captionEndDeco.getOffsetWidth() > 0;
    }
}
