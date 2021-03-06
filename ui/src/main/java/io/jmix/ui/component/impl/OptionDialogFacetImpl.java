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

package io.jmix.ui.component.impl;

import io.jmix.ui.Dialogs;
import io.jmix.ui.GuiDevelopmentException;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.UiControllerUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class OptionDialogFacetImpl extends AbstractFacet implements OptionDialogFacet {

    protected String caption;
    protected String message;

    protected SizeWithUnit width;
    protected SizeWithUnit height;

    protected String styleName;

    protected ContentMode contentMode = ContentMode.TEXT;

    protected WindowMode windowMode = WindowMode.NORMAL;

    protected String actionId;
    protected String buttonId;

    protected Collection<DialogAction<OptionDialogFacet>> actions;

    protected boolean htmlSanitizerEnabled;

    @Override
    public void setActions(@Nullable Collection<DialogAction<OptionDialogFacet>> actions) {
        this.actions = actions;
    }

    @Nullable
    @Override
    public Collection<DialogAction<OptionDialogFacet>> getActions() {
        return actions;
    }

    @Override
    public void setCaption(@Nullable String caption) {
        this.caption = caption;
    }

    @Nullable
    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setContentMode(ContentMode contentMode) {
        this.contentMode = contentMode;
    }

    @Override
    public ContentMode getContentMode() {
        return contentMode;
    }

    @Override
    public void setWindowMode(WindowMode windowMode) {
        this.windowMode = windowMode;
    }

    @Override
    public WindowMode getWindowMode() {
        return windowMode;
    }

    @Override
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    @Nullable
    @Override
    public String getStyleName() {
        return styleName;
    }

    @Override
    public void setWidth(@Nullable String width) {
        this.width = SizeWithUnit.parseStringSize(width);
    }

    @Override
    public float getWidth() {
        return width.getSize();
    }

    @Override
    public SizeUnit getWidthSizeUnit() {
        return width.getUnit();
    }

    @Override
    public void setHeight(@Nullable String height) {
        this.height = SizeWithUnit.parseStringSize(height);
    }

    @Override
    public float getHeight() {
        return height.getSize();
    }

    @Override
    public SizeUnit getHeightSizeUnit() {
        return height.getUnit();
    }

    @Nullable
    @Override
    public String getActionTarget() {
        return actionId;
    }

    @Override
    public void setActionTarget(@Nullable String actionId) {
        this.actionId = actionId;
    }

    @Nullable
    @Override
    public String getButtonTarget() {
        return buttonId;
    }

    @Override
    public void setButtonTarget(@Nullable String buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void setHtmlSanitizerEnabled(boolean htmlSanitizerEnabled) {
        this.htmlSanitizerEnabled = htmlSanitizerEnabled;
    }

    @Override
    public boolean isHtmlSanitizerEnabled() {
        return htmlSanitizerEnabled;
    }

    @Override
    public void show() {
        Frame owner = getOwner();
        if (owner == null) {
            throw new IllegalStateException("OptionDialog is not attached to Frame");
        }

        Dialogs.OptionDialogBuilder builder = UiControllerUtils.getScreenContext(owner.getFrameOwner())
                .getDialogs()
                .createOptionDialog();

        if (width != null) {
            builder.withWidth(width.stringValue());
        }
        if (height != null) {
            builder.withHeight(height.stringValue());
        }

        builder.withCaption(caption)
                .withMessage(message)
                .withContentMode(contentMode)
                .withHtmlSanitizer(htmlSanitizerEnabled)
                .withWindowMode(windowMode)
                .withStyleName(styleName)
                .withActions(createActions())
                .show();
    }

    @Nullable
    @Override
    public Object getSubPart(String name) {
        if (actions == null) {
            return null;
        }
        return actions.stream()
                .filter(action -> Objects.equals(action.getId(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setOwner(@Nullable Frame owner) {
        super.setOwner(owner);

        subscribe();
    }

    protected void subscribe() {
        Frame owner = getOwner();
        if (owner == null) {
            throw new IllegalStateException("Notification is not attached to Frame");
        }

        if (isNotEmpty(actionId)
                && isNotEmpty(buttonId)) {
            throw new GuiDevelopmentException(
                    "Notification facet should have either action or button target",
                    owner.getId());
        }

        if (isNotEmpty(actionId)) {
            subscribeOnAction(owner);
        } else if (isNotEmpty(buttonId)) {
            subscribeOnButton(owner);
        }
    }

    protected void subscribeOnAction(Frame owner) {
        Action action = ComponentsHelper.findAction(owner, actionId);

        if (!(action instanceof BaseAction)) {
            throw new GuiDevelopmentException(
                    String.format("Unable to find Dialog target button with id '%s'", actionId),
                    owner.getId());
        }

        ((BaseAction) action).addActionPerformedListener(e ->
                show());
    }

    protected void subscribeOnButton(Frame owner) {
        Component component = owner.getComponent(buttonId);

        if (!(component instanceof Button)) {
            throw new GuiDevelopmentException(
                    String.format("Unable to find Dialog target button with id '%s'", buttonId),
                    owner.getId());
        }

        ((Button) component).addClickListener(e ->
                show());
    }

    protected Action[] createActions() {
        if (actions == null) {
            return new Action[]{};
        }
        return actions.stream()
                .map(this::createAction)
                .collect(Collectors.toList())
                .toArray(new Action[]{});
    }

    protected BaseAction createAction(DialogAction<OptionDialogFacet> action) {
        return new BaseAction(action.getId())
                .withCaption(action.getCaption())
                .withDescription(action.getDescription())
                .withIcon(action.getIcon())
                .withPrimary(action.isPrimary())
                .withHandler(evt -> {
                    if (action.getActionHandler() != null) {
                        action.getActionHandler().accept(
                                new DialogActionPerformedEvent<>(this, action));
                    }
                });
    }
}
