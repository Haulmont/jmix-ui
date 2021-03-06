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

package io.jmix.ui.app.bulk;

import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.ui.component.ListComponent;
import io.jmix.ui.component.validation.Validator;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.jmix.core.common.util.Preconditions.checkNotNullArgument;

public interface BulkEditorController<V> {

    void setBulkEditorContext(BulkEditorContext<V> context);

    class BulkEditorContext<V> {
        protected final MetaClass metaClass;
        protected final Collection<V> selected;

        protected ListComponent<V> listComponent;

        protected String exclude;
        protected List<String> includeProperties = Collections.emptyList();
        protected Map<String, Validator<?>> fieldValidators;
        protected List<Validator<?>> modelValidators;
        protected boolean useConfirmDialog;
        protected FieldSorter fieldSorter;
        protected ColumnsMode columnsMode;

        public BulkEditorContext(MetaClass metaClass, Collection<V> selected) {
            checkNotNullArgument(metaClass);
            checkNotNullArgument(selected);

            this.metaClass = metaClass;
            this.selected = selected;
        }

        public MetaClass getMetaClass() {
            return metaClass;
        }

        public Collection<V> getSelected() {
            return selected;
        }

        @Nullable
        public ListComponent<V> getListComponent() {
            return listComponent;
        }

        public void setListComponent(@Nullable ListComponent<V> listComponent) {
            this.listComponent = listComponent;
        }

        @Nullable
        public String getExclude() {
            return exclude;
        }

        public void setExclude(@Nullable String exclude) {
            this.exclude = exclude;
        }

        public List<String> getIncludeProperties() {
            return includeProperties != null
                    ? Collections.unmodifiableList(includeProperties)
                    : Collections.emptyList();
        }

        public void setIncludeProperties(@Nullable List<String> includeProperties) {
            this.includeProperties = includeProperties;
        }

        public Map<String, Validator<?>> getFieldValidators() {
            return fieldValidators != null
                    ? Collections.unmodifiableMap(fieldValidators)
                    : Collections.emptyMap();
        }

        public void setFieldValidators(@Nullable Map<String, Validator<?>> fieldValidators) {
            this.fieldValidators = fieldValidators;
        }

        public List<Validator<?>> getModelValidators() {
            return modelValidators != null
                    ? Collections.unmodifiableList(modelValidators)
                    : Collections.emptyList();
        }

        public void setModelValidators(@Nullable List<Validator<?>> modelValidators) {
            this.modelValidators = modelValidators;
        }

        public boolean isUseConfirmDialog() {
            return useConfirmDialog;
        }

        public void setUseConfirmDialog(boolean useConfirmDialog) {
            this.useConfirmDialog = useConfirmDialog;
        }

        @Nullable
        public FieldSorter getFieldSorter() {
            return fieldSorter;
        }

        public void setFieldSorter(@Nullable FieldSorter fieldSorter) {
            this.fieldSorter = fieldSorter;
        }

        @Nullable
        public ColumnsMode getColumnsMode() {
            return columnsMode;
        }

        public void setColumnsMode(@Nullable ColumnsMode columnsMode) {
            this.columnsMode = columnsMode;
        }
    }
}
