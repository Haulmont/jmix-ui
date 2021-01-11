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

package io.jmix.ui.app.systeminfo;

import io.jmix.core.*;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.ui.Facets;
import io.jmix.ui.Notifications;
import io.jmix.ui.Notifications.NotificationType;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiProperties;
import io.jmix.ui.app.systeminfo.model.InfoParam;
import io.jmix.ui.component.*;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@UiController("sysInfoWindow")
@UiDescriptor("system-info-window.xml")
public class SystemInfoWindow extends Screen {

    protected static final String TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    protected Table<InfoParam> infoTable;
    @Autowired
    protected TextArea<String> scriptArea;
    @Autowired
    protected HBoxLayout buttonsPanel;

    @Autowired
    protected CollectionContainer<InfoParam> paramsDc;

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected Notifications notifications;
    @Autowired(required = false)
    protected EntitySqlGenerationService sqlGenerationService;


    protected Button copyButton;

    protected Object entity;

    @Subscribe
    public void onInit(InitEvent event) {
        initCopyButton();
        initTable();
    }

    protected void initCopyButton() {
        Facets facets = getApplicationContext().getBean(Facets.class);
        ClipboardTrigger clipboardTrigger = facets.create(ClipboardTrigger.class);

        if (clipboardTrigger.isSupportedByWebBrowser()) {
            clipboardTrigger.setId("clipboardTrigger");

            copyButton = uiComponents.create(Button.class);

            copyButton.setId("copyBtn");
            copyButton.setIconFromSet(JmixIcon.CLIPBOARD);
            copyButton.setVisible(false);
            copyButton.setDescription(messageBundle.getMessage("copyBtn.description"));

            buttonsPanel.add(copyButton);

            clipboardTrigger.setInput(scriptArea);
            clipboardTrigger.setButton(copyButton);
            clipboardTrigger.addCopyListener(this::onCopy);

            getWindow().addFacet(clipboardTrigger);
        }
    }

    protected void initTable() {
        infoTable.removeAllActions();
    }

    protected void onCopy(ClipboardTrigger.CopyEvent copyEvent) {
        notifications.create()
                .withCaption(messageBundle.getMessage(copyEvent.isSuccess()
                        ? "systemInfoWindow.copingSuccessful"
                        : "systemInfoWindow.copingFailed"))
                .withType(NotificationType.TRAY)
                .show();
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (entity == null) {
            throw new IllegalStateException("No entity is not passed. Can't collect system info");
        }

        initButtonsPanel();
        collectEntityInfo();
    }

    protected void initButtonsPanel() {
        UiProperties uiProperties = getApplicationContext().getBean(UiProperties.class);

        if (sqlGenerationService == null
                || !uiProperties.isSystemInfoScriptsEnabled()
                || !metadataTools.isPersistent(entity.getClass())) {
            buttonsPanel.setVisible(false);
        }
    }

    protected void collectEntityInfo() {
        EntityStates entityStates = getApplicationContext().getBean(EntityStates.class);
        boolean isNewEntity = entityStates.isNew(entity);

        if (!isNewEntity) {
            entity = reloadEntity(entity);
        }

        MetaClass metaClass = metadata.getClass(entity);

        List<InfoParam> items = new ArrayList<>();
        items.add(createItem("systemInfo.entityName", metaClass.getName()));

        Class<?> javaClass = metaClass.getJavaClass();
        items.add(createItem("systemInfo.entityClass", javaClass.getName()));

        if ((metadataTools.isEmbeddable(metaClass) || metadataTools.isPersistent(metaClass))
                && isNewEntity) {
            items.add(createItem("systemInfo.state",
                    messageBundle.getMessage("systemInfo.isNew")));
        }

        if (metadataTools.isEmbeddable(metaClass)) {
            items.add(createItem("systemInfo.specificInstance",
                    messageBundle.getMessage("systemInfo.embeddableInstance")));
        } else if (!metadataTools.isPersistent(metaClass)) {
            items.add(createItem("systemInfo.specificInstance",
                    messageBundle.getMessage("systemInfo.nonPersistentInstance")));
        }

        addItem(items, metadataTools.getDatabaseTable(metaClass), "systemInfo.entityTable");
        addItem(items, EntityValues.getId(entity), "systemInfo.id");
        addItem(items, EntityValues.getVersion(entity), "systemInfo.version");

        SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_DATE_FORMAT);
        if (EntityValues.isAuditSupported(entity)) {
            addItem(items, entity, "createdDate", "systemInfo.createdDate", df::format);
            addItem(items, entity, "createdBy", "systemInfo.createdBy");

            addItem(items, entity, "lastModifiedDate", "systemInfo.lastModifiedDate", df::format);
            addItem(items, entity, "lastModifiedBy", "systemInfo.lastModifiedBy");
        }

        if (EntityValues.isSoftDeleted(entity)) {
            addItem(items, entity, "deletedDate", "systemInfo.deletedDate", df::format);
            addItem(items, entity, "deletedBy", "systemInfo.deletedBy");
        }

        paramsDc.setItems(items);
    }

    protected void addItem(List<InfoParam> items, Object entity, String param, String messageKey) {
        addItem(items, entity, param, messageKey, null);
    }

    protected void addItem(List<InfoParam> items, Object entity, String param, String messageKey,
                           @Nullable Function<Object, String> formatter) {
        Object value = EntityValues.getValue(entity, param);
        addItem(items, value, messageKey, formatter);
    }

    protected void addItem(List<InfoParam> items, Object value, String messageKey) {
        addItem(items, value, messageKey, (Function<Object, String>) null);
    }

    protected void addItem(List<InfoParam> items, Object value, String messageKey,
                           @Nullable Function<Object, String> formatter) {
        if (value != null) {
            items.add(createItem(messageKey, formatter != null
                    ? formatter.apply(value)
                    : value));
        }
    }

    protected Object reloadEntity(Object entity) {
        Object id = EntityValues.getId(entity);
        if (id == null) {
            return entity;
        }

        FetchPlans fetchPlans = getApplicationContext().getBean(FetchPlans.class);
        FetchPlan fetchPlan = fetchPlans.builder(entity.getClass())
                .addSystem()
                .build();

        DataManager dataManager = getApplicationContext().getBean(DataManager.class);
        return dataManager.load(entity.getClass())
                .id(id)
                .fetchPlan(fetchPlan)
                .one();
    }

    protected InfoParam createItem(String messageKey, Object value) {
        InfoParam item = metadata.create(InfoParam.class);
        item.setKey(messageBundle.getMessage(messageKey));
        item.setValue(metadataTools.format(value));

        return item;
    }

    @Subscribe("insertBtn")
    public void onInsertBtnClick(Button.ClickEvent event) {
        updateButtons(event.getButton());
        updateScriptArea(sqlGenerationService.generateInsertScript(entity));
    }

    @Subscribe("selectBtn")
    public void onSelectBtnClick(Button.ClickEvent event) {
        updateButtons(event.getButton());
        updateScriptArea(sqlGenerationService.generateSelectScript(entity));
    }

    @Subscribe("updateBtn")
    public void onUpdateBtnClick(Button.ClickEvent event) {
        updateButtons(event.getButton());
        updateScriptArea(sqlGenerationService.generateUpdateScript(entity));
    }

    protected void updateScriptArea(String value) {
        scriptArea.setValue(value);

        if (!scriptArea.isVisible()) {
            scriptArea.setVisible(true);

            if (getWindow() instanceof DialogWindow) {
                getWindow().resetExpanded();
                getWindow().expand(scriptArea);
                ((DialogWindow) getWindow()).setDialogHeight("550px");
            } else {
                scriptArea.setHeight("150px");
            }
        }
    }

    protected void updateButtons(Button caller) {
        if (copyButton != null && !copyButton.isVisible()) {
            copyButton.setVisible(true);
        }

        buttonsPanel.getOwnComponents().stream()
                .filter(component -> component != caller
                        && !component.isEnabled())
                .forEach(component -> component.setEnabled(true));
    }
}
