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
package io.jmix.ui.component.impl;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Layout;
import org.springframework.context.ApplicationContext;
import io.jmix.core.common.event.EventHub;
import io.jmix.core.common.event.Subscription;
import io.jmix.ui.AppUI;
import io.jmix.ui.UiProperties;
import io.jmix.ui.component.*;
import io.jmix.ui.icon.IconResolver;
import io.jmix.ui.icon.Icons;
import io.jmix.ui.sanitizer.HtmlSanitizer;
import io.jmix.ui.sys.TestIdManager;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class WebAbstractComponent<T extends com.vaadin.ui.Component>
        implements Component, Component.Wrapper, Component.HasXmlDescriptor, Component.BelongToFrame, Component.HasIcon,
                   Component.HasCaption, HasDebugId, HasContextHelp, HasHtmlCaption, HasHtmlDescription, AttachNotifier,
                   HasHtmlSanitizer {

    public static final String ICON_STYLE = "icon";

    protected String id;
    protected T component;

    protected Element element;
    protected Frame frame;
    protected Component parent;

    protected Alignment alignment = Alignment.TOP_LEFT;
    protected String icon;

    protected boolean descriptionAsHtml = false;

    protected Boolean htmlSanitizerEnabled;

    protected Consumer<ContextHelpIconClickEvent> contextHelpIconClickHandler;
    protected Registration contextHelpIconClickListener;

    protected ApplicationContext applicationContext;

    // private, lazily initialized
    private EventHub eventHub = null;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected EventHub getEventHub() {
        if (eventHub == null) {
            eventHub = new EventHub();
        }
        return eventHub;
    }

    protected <E> void publish(Class<E> eventType, E event) {
        if (eventHub != null) {
            eventHub.publish(eventType, event);
        }
    }

    protected boolean hasSubscriptions(Class<?> eventClass) {
        return eventHub != null && eventHub.hasSubscriptions(eventClass);
    }

    protected <E> boolean unsubscribe(Class<E> eventType, Consumer<E> listener) {
        if (eventHub != null) {
            return eventHub.unsubscribe(eventType, listener);
        }
        return false;
    }

    @Nullable
    @Override
    public Frame getFrame() {
        return frame;
    }

    @Override
    public void setFrame(@Nullable Frame frame) {
        this.frame = frame;

        if (frame instanceof FrameImplementation) {
            ((FrameImplementation) frame).registerComponent(this);
        }

        if (getDebugId() == null) {
            assignDebugId();
        }
    }

    @Override
    public boolean isResponsive() {
        com.vaadin.ui.Component composition = getComposition();
        if (composition instanceof AbstractComponent) {
            return ((AbstractComponent) composition).isResponsive();
        }
        return false;
    }

    @Override
    public void setResponsive(boolean responsive) {
        com.vaadin.ui.Component composition = getComposition();

        if (composition instanceof AbstractComponent) {
            ((AbstractComponent) composition).setResponsive(responsive);
        }
    }

    @Nullable
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(@Nullable String id) {
        if (!Objects.equals(this.id, id)) {
            if (frame != null) {
                ((FrameImplementation) frame).unregisterComponent(this);
            }

            this.id = id;

            AppUI ui = AppUI.getCurrent();
            if (ui != null) {
                if (this.component != null && ui.isTestMode()) {
                    this.component.setJTestId(id);
                }
            }

            assignDebugId();

            if (frame != null) {
                ((FrameImplementation) frame).registerComponent(this);
            }
        }
    }

    protected void assignDebugId() {
        AppUI ui = AppUI.getCurrent();
        if (ui == null) {
            return;
        }

        if (this.component == null
                || frame == null
                || StringUtils.isEmpty(frame.getId())) {
            return;
        }

        if (ui.isPerformanceTestMode() && getDebugId() == null) {
            String fullFrameId = ComponentsHelper.getFullFrameId(frame);
            TestIdManager testIdManager = ui.getTestIdManager();

            String alternativeId = id != null ? id : getClass().getSimpleName();
            String candidateId = fullFrameId + "." + alternativeId;

            setDebugId(testIdManager.getTestId(candidateId));
        }
    }

    @Nullable
    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable Component parent) {
        if (this.parent != parent) {
            if (isAttached()) {
                detached();
            }

            this.parent = parent;

            if (isAttached()) {
                attached();
            }
        }
    }

    @Override
    public boolean isAttached() {
        Component current = parent;
        while (current != null) {
            if (current instanceof Window) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    @Override
    public void attached() {
        if (hasSubscriptions(AttachEvent.class)) {
            publish(AttachEvent.class, new AttachEvent(this));
        }
    }

    @Override
    public void detached() {
        if (hasSubscriptions(DetachEvent.class)) {
            publish(DetachEvent.class, new DetachEvent(this));
        }
    }

    @Override
    public Subscription addAttachListener(Consumer<AttachEvent> listener) {
        return getEventHub().subscribe(AttachEvent.class, listener);
    }

    @Override
    public Subscription addDetachListener(Consumer<DetachEvent> listener) {
        return getEventHub().subscribe(DetachEvent.class, listener);
    }

    @Nullable
    @Override
    public String getDebugId() {
        return component.getId();
    }

    @Override
    public void setDebugId(@Nullable String id) {
        component.setId(id);
    }

    @Override
    public String getStyleName() {
        return getComposition().getStyleName();
    }

    @Override
    public void setStyleName(@Nullable String name) {
        getComposition().setStyleName(name);
    }

    @Override
    public void addStyleName(String styleName) {
        getComposition().addStyleName(styleName);
    }

    @Override
    public void removeStyleName(String styleName) {
        getComposition().removeStyleName(styleName);
    }

    @Override
    public boolean isEnabled() {
        return getComposition().isEnabled();
    }

    @Override
    public boolean isEnabledRecursive() {
        return WebComponentsHelper.isComponentEnabled(getComposition());
    }

    @Override
    public void setEnabled(boolean enabled) {
        getComposition().setEnabled(enabled);
    }

    @Override
    public boolean isVisible() {
        return getComposition().isVisible();
    }

    @Override
    public boolean isVisibleRecursive() {
        return WebComponentsHelper.isComponentVisible(getComposition());
    }

    @Override
    public void setVisible(boolean visible) {
        getComposition().setVisible(visible);
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public String getCaption() {
        return component.getCaption();
    }

    @Override
    public void setCaption(@Nullable String caption) {
        if (isCaptionAsHtml()) {
            caption = sanitize(caption);
        }

        component.setCaption(caption);
    }

    @Override
    public boolean isCaptionAsHtml() {
        return ((AbstractComponent) component).isCaptionAsHtml();
    }

    @Override
    public void setCaptionAsHtml(boolean captionAsHtml) {
        if (isCaptionAsHtml() != captionAsHtml) {
            ((AbstractComponent) component).setCaptionAsHtml(captionAsHtml);

            setCaption(getCaption());
        }
    }

    @Nullable
    @Override
    public String getDescription() {
        return component.getDescription();
    }

    @Override
    public void setDescription(@Nullable String description) {
        if (isDescriptionAsHtml()) {
            description = sanitize(description);
        }

        ((AbstractComponent) component).setDescription(description, descriptionAsHtml
                ? com.vaadin.shared.ui.ContentMode.HTML
                : com.vaadin.shared.ui.ContentMode.PREFORMATTED);
    }

    @Override
    public boolean isDescriptionAsHtml() {
        return descriptionAsHtml;
    }

    @Override
    public void setDescriptionAsHtml(boolean descriptionAsHtml) {
        if (this.descriptionAsHtml != descriptionAsHtml) {
            this.descriptionAsHtml = descriptionAsHtml;
            // Trigger component changes
            setDescription(getDescription());
        }
    }

    @Override
    public void setIcon(@Nullable String icon) {
        this.icon = icon;

        if (StringUtils.isNotEmpty(icon)) {
            Resource iconResource = getIconResource(icon);
            getComposition().setIcon(iconResource);
            getComposition().addStyleName(ICON_STYLE);
        } else {
            getComposition().setIcon(null);
            getComposition().removeStyleName(ICON_STYLE);
        }
    }

    @Override
    public void setIconFromSet(@Nullable Icons.Icon icon) {
        String iconName = getIconName(icon);
        setIcon(iconName);
    }

    @Nullable
    protected Resource getIconResource(String icon) {
        return applicationContext.getBean(IconResolver.class).getIconResource(icon);
    }

    @Nullable
    protected String getIconName(@Nullable Icons.Icon icon) {
        return applicationContext.getBean(Icons.class).get(icon);
    }

    @Override
    public float getHeight() {
        return getComposition().getHeight();
    }

    @Override
    public SizeUnit getHeightSizeUnit() {
        return WebWrapperUtils.toSizeUnit(getComposition().getHeightUnits());
    }

    @Override
    public void setHeight(@Nullable String height) {
        // do not try to parse string if constant passed
        if (Component.AUTO_SIZE.equals(height)) {
            getComposition().setHeight(-1, Sizeable.Unit.PIXELS);
        } else if (Component.FULL_SIZE.equals(height)) {
            getComposition().setHeight(100, Sizeable.Unit.PERCENTAGE);
        } else {
            getComposition().setHeight(height);
        }
    }

    @Override
    public float getWidth() {
        return getComposition().getWidth();
    }

    @Override
    public SizeUnit getWidthSizeUnit() {
        return WebWrapperUtils.toSizeUnit(getComposition().getWidthUnits());
    }

    @Override
    public void setWidth(@Nullable String width) {
        // do not try to parse string if constant passed
        if (Component.AUTO_SIZE.equals(width)) {
            getComposition().setWidth(-1, Sizeable.Unit.PIXELS);
        } else if (Component.FULL_SIZE.equals(width)) {
            getComposition().setWidth(100, Sizeable.Unit.PERCENTAGE);
        } else {
            getComposition().setWidth(width);
        }
    }

    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;

        if (getComposition().getParent() != null) {
            com.vaadin.ui.Component component = this.getComposition().getParent();
            if (component instanceof Layout.AlignmentHandler) {
                ((Layout.AlignmentHandler) component).setComponentAlignment(this.getComposition(),
                        WebWrapperUtils.toVaadinAlignment(alignment));
            }
        }
    }

    @Override
    public T getComponent() {
        return component;
    }

    @Override
    public com.vaadin.ui.Component getComposition() {
        return component;
    }

    @Nullable
    @Override
    public Element getXmlDescriptor() {
        return element;
    }

    @Override
    public void setXmlDescriptor(@Nullable Element element) {
        this.element = element;
    }

    @Nullable
    @Override
    public String getContextHelpText() {
        return ((AbstractComponent) getComposition()).getContextHelpText();
    }

    @Override
    public void setContextHelpText(@Nullable String contextHelpText) {
        if (isContextHelpTextHtmlEnabled()) {
            contextHelpText = sanitize(contextHelpText);
        }

        ((AbstractComponent) getComposition()).setContextHelpText(contextHelpText);
    }

    @Override
    public boolean isContextHelpTextHtmlEnabled() {
        return ((AbstractComponent) getComposition()).isContextHelpTextHtmlEnabled();
    }

    @Override
    public void setContextHelpTextHtmlEnabled(boolean enabled) {
        if (isContextHelpTextHtmlEnabled() != enabled) {
            ((AbstractComponent) getComposition()).setContextHelpTextHtmlEnabled(enabled);

            setContextHelpText(getContextHelpText());
        }
    }

    @Nullable
    @Override
    public Consumer<ContextHelpIconClickEvent> getContextHelpIconClickHandler() {
        return contextHelpIconClickHandler;
    }

    @Override
    public void setContextHelpIconClickHandler(@Nullable Consumer<ContextHelpIconClickEvent> handler) {
        if (!Objects.equals(this.contextHelpIconClickHandler, handler)) {
            this.contextHelpIconClickHandler = handler;

            if (handler == null) {
                contextHelpIconClickListener.remove();
                contextHelpIconClickListener = null;
            } else if (contextHelpIconClickListener == null) {
                contextHelpIconClickListener = ((AbstractComponent) getComposition())
                        .addContextHelpIconClickListener(this::onContextHelpIconClick);
            }
        }
    }

    protected void onContextHelpIconClick(@SuppressWarnings("unused") com.vaadin.ui.Component.HasContextHelp.ContextHelpIconClickEvent e) {
        if (contextHelpIconClickHandler != null) {
            ContextHelpIconClickEvent event = new ContextHelpIconClickEvent(this);
            contextHelpIconClickHandler.accept(event);
        }
    }

    @Override
    public <X> X unwrap(Class<X> internalComponentClass) {
        return internalComponentClass.cast (getComponent());
    }

    @Override
    @Nullable
    public <X> X unwrapOrNull(Class<X> internalComponentClass) {
        return internalComponentClass.isAssignableFrom(getComponent().getClass())
                ? internalComponentClass.cast(getComponent())
                : null;
    }

    @Override
    public <X> void withUnwrapped(Class<X> internalComponentClass, Consumer<X> action) {
        if (internalComponentClass.isAssignableFrom(getComponent().getClass())) {
            action.accept(internalComponentClass.cast(getComponent()));
        }
    }

    @Override
    public <X> X unwrapComposition(Class<X> internalCompositionClass) {
        return internalCompositionClass.cast(getComposition());
    }

    @Nullable
    @Override
    public <X> X unwrapCompositionOrNull(Class<X> internalCompositionClass) {
        return internalCompositionClass.isAssignableFrom(getComposition().getClass())
                ? internalCompositionClass.cast(getComposition())
                : null;
    }

    @Override
    public <X> void withUnwrappedComposition(Class<X> internalCompositionClass, Consumer<X> action) {
        if (internalCompositionClass.isAssignableFrom(getComposition().getClass())) {
            action.accept(internalCompositionClass.cast(getComposition()));
        }
    }

    protected boolean hasValidationError() {
        if (getComposition() instanceof AbstractComponent) {
            AbstractComponent composition = (AbstractComponent) getComposition();
            return composition.getComponentError() instanceof UserError;
        }
        return false;
    }

    protected void setValidationError(@Nullable String errorMessage) {
        if (getComposition() instanceof AbstractComponent) {
            AbstractComponent composition = (AbstractComponent) getComposition();
            if (errorMessage == null) {
                composition.setComponentError(null);
            } else {
                composition.setComponentError(new UserError(errorMessage));
            }
        }
    }

    @Override
    public boolean isHtmlSanitizerEnabled() {
        return htmlSanitizerEnabled != null
                ? htmlSanitizerEnabled
                : getUiProperties().isHtmlSanitizerEnabled();
    }

    @Override
    public void setHtmlSanitizerEnabled(boolean htmlSanitizerEnabled) {
        if (!Objects.equals(this.htmlSanitizerEnabled, htmlSanitizerEnabled)) {
            this.htmlSanitizerEnabled = htmlSanitizerEnabled;

            setCaption(getCaption());
            setDescription(getDescription());
            setContextHelpText(getContextHelpText());
        }
    }

    @Nullable
    protected String sanitize(@Nullable String html) {
        return isHtmlSanitizerEnabled()
                ? getHtmlSanitizer().sanitize(html)
                : html;
    }

    protected UiProperties getUiProperties() {
        return applicationContext.getBean(UiProperties.class);
    }

    protected HtmlSanitizer getHtmlSanitizer() {
        return applicationContext.getBean(HtmlSanitizer.class);
    }
}
