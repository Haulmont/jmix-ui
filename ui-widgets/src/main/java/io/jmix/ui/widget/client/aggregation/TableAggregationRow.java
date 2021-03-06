/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package io.jmix.ui.widget.client.aggregation;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import io.jmix.ui.widget.client.Tools;
import io.jmix.ui.widget.client.table.JmixScrollTableWidget;
import io.jmix.ui.widget.client.tableshared.TableWidget;
import io.jmix.ui.widget.client.tableshared.TotalAggregationInputListener;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.UIDL;
import com.vaadin.v7.client.ui.VScrollTable;
import io.jmix.ui.widget.client.treetable.JmixTreeTableWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Special aggregation row for {@link JmixScrollTableWidget} and
 * {@link JmixTreeTableWidget}
 */
public class TableAggregationRow extends Panel {

    protected boolean initialized = false;

    protected char[] aligns;
    protected Element container;
    protected Element tr;

    protected TableWidget tableWidget;

    protected TotalAggregationInputListener totalAggregationInputHandler;
    protected List<AggregationInputFieldInfo> inputsList;

    public TableAggregationRow(TableWidget tableWidget) {
        this.tableWidget = tableWidget;

        String primaryStyleName = tableWidget.getStylePrimaryName();

        DivElement wrapElement = Document.get().createDivElement();
        wrapElement.setClassName(primaryStyleName + "-arow-wrap");
        setElement(wrapElement);

        container = Document.get().createDivElement();

        container.setClassName(primaryStyleName + "-arow");
        container.getStyle().setOverflow(Overflow.HIDDEN);

        wrapElement.appendChild(container);
    }

    @Override
    public Iterator<Widget> iterator() {
        return Collections.<Widget>emptyList().iterator();
    }

    @Override
    public boolean remove(Widget child) {
        return false;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        final int type = DOM.eventGetType(event);
        Element sourceElement = Element.as(event.getEventTarget());

        if (type == Event.ONKEYDOWN
                && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
            AggregationInputFieldInfo info = getAggregationInputInfo(sourceElement);
            if (info != null) {
                String columnKey = info.getColumnKey();
                String value = info.getInputElement().getValue();
                info.setFocused(true);

                if (columnKey != null) {
                    totalAggregationInputHandler.onInputChange(columnKey, value, true);
                }
            }
        }


        if (type == Event.ONCHANGE && totalAggregationInputHandler != null) {
            AggregationInputFieldInfo info = getAggregationInputInfo(sourceElement);
            if (info != null) {
                String columnKey = info.getColumnKey();
                String value = info.getInputElement().getValue();
                // do not send event, cause it was sent with `ENTER` key event
                if (info.isFocused()) {
                    info.setFocused(false);
                    return;
                }

                if (columnKey != null) {
                    totalAggregationInputHandler.onInputChange(columnKey, value, false);
                }
            }
        }
    }

    public void updateFromUIDL(UIDL uidl) {
        if (container.hasChildNodes()) {
            container.removeAllChildren();
        }

        aligns = tableWidget.getHead().getColumnAlignments();

        if (uidl.getChildCount() > 0) {
            final Element table = DOM.createTable();
            table.setAttribute("cellpadding", "0");
            table.setAttribute("cellspacing", "0");

            final Element tBody = DOM.createTBody();
            tr = DOM.createTR();

            tr.setClassName(tableWidget.getStylePrimaryName() + "-arow-row");

            if (inputsList != null && !inputsList.isEmpty()) {
                inputsList.clear();
            }

            addCellsFromUIDL(uidl);

            tBody.appendChild(tr);
            table.appendChild(tBody);

            DivElement tableWrapper = Document.get().createDivElement();
            tableWrapper.appendChild(table);
            container.appendChild(tableWrapper);

            // set focus to input if we pressed `ENTER`
            String focusColumnKey = uidl.getStringAttribute("focusInput");
            if (focusColumnKey != null && inputsList != null) {
                for (AggregationInputFieldInfo info : inputsList) {
                    if (info.getColumnKey().equals(focusColumnKey)) {
                        info.getInputElement().focus();
                        break;
                    }
                }
            }
        }

        initialized = container.hasChildNodes();
    }

    public void setCellWidth(int cellIx, int width) {
        // CAUTION: copied from VScrollTableRow with small changes
        final Element cell = DOM.getChild(tr, cellIx);
        Style wrapperStyle = cell.getFirstChildElement().getStyle();
        int wrapperWidth = width;
        if (BrowserInfo.get().isWebkit()
                || BrowserInfo.get().isOpera10()) {
            /*
             * Some versions of Webkit and Opera ignore the width
             * definition of zero width table cells. Instead, use 1px
             * and compensate with a negative margin.
             */
            if (width == 0) {
                wrapperWidth = 1;
                wrapperStyle.setMarginRight(-1, Style.Unit.PX);
            } else {
                wrapperStyle.clearMarginRight();
            }
        }
        wrapperStyle.setPropertyPx("width", wrapperWidth);
        cell.getStyle().setPropertyPx("width", width);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setHorizontalScrollPosition(int scrollLeft) {
        container.setScrollLeft(scrollLeft);
    }

    public void setTotalAggregationInputHandler(TotalAggregationInputListener totalAggregationInputHandler) {
        this.totalAggregationInputHandler = totalAggregationInputHandler;
    }

    public boolean isAggregationRowEditable() {
        return inputsList != null && !inputsList.isEmpty();
    }

    // CAUTION copied from com.vaadin.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow.getRealCellWidth
    public double getRealCellWidth(int colIdx) {
        if (colIdx >= tr.getChildCount()) {
            return -1;
        }

        Element cell = DOM.getChild(tr, colIdx);
        ComputedStyle cs = new ComputedStyle(cell);

        return cs.getWidth() + cs.getPaddingWidth() + cs.getBorderWidth();
    }

    protected void addCellsFromUIDL(UIDL uidl) {
        int colIndex = 0;
        final Iterator cells = uidl.getChildIterator();
        while (cells.hasNext() && colIndex < tableWidget.getVisibleColOrder().length) {
            String columnId = tableWidget.getVisibleColOrder()[colIndex];

            if (addSpecificCell(columnId, colIndex)) {
                colIndex++;
                continue;
            }

            final Object cell = cells.next();

            String style = "";
            if (uidl.hasAttribute("style-" + columnId)) {
                style = uidl.getStringAttribute("style-" + columnId);
            }

            boolean sorted = tableWidget.getHead().getHeaderCell(colIndex).isSorted();

            if (isAggregationEditable(uidl, colIndex)) {
                addCellWithField((String) cell, aligns[colIndex], colIndex);
            } else if (cell instanceof String) {
                addCell((String) cell, aligns[colIndex], style, sorted);
            }

            final String colKey = tableWidget.getColKeyByIndex(colIndex);
            int colWidth;
            if ((colWidth = tableWidget.getColWidth(colKey)) > -1) {
                tableWidget.setColWidth(colIndex, colWidth, false);
            }

            colIndex++;
        }
    }

    protected boolean isAggregationEditable(UIDL uidl, int colIndex) {
        UIDL colUidl = uidl.getChildByTagName("editableAggregationColumns");
        if (colUidl == null) {
            return false;
        }
        String colKey = tableWidget.getColKeyByIndex(colIndex);
        Iterator iterator = colUidl.getChildIterator();
        while (iterator.hasNext()) {
            Object uidlKey = iterator.next();
            if (uidlKey.equals(colKey)) {
                return true;
            }
        }
        return false;
    }

    // Extension point for GroupTable divider column
    protected boolean addSpecificCell(String columnId, int colIndex) {
        return false;
    }

    protected void addCellWithField(String text, char align, int colIndex) {
        final TableCellElement td = DOM.createTD().cast();
        final DivElement container = DOM.createDiv().cast();
        container.setClassName(tableWidget.getStylePrimaryName() + "-cell-wrapper" + " " + "widget-container");

        setAlign(align, container);

        InputElement inputElement = DOM.createInputText().cast();
        inputElement.setValue(text);
        inputElement.addClassName("v-textfield v-widget");
        inputElement.addClassName("jmix-total-aggregation-textfield");
        Style elemStyle = inputElement.getStyle();
        elemStyle.setWidth(100, Style.Unit.PCT);

        VScrollTable.HeaderCell headerCell = tableWidget.getHead().getHeaderCell(colIndex);
        if (headerCell != null) {
            String headerCellJTEstId = headerCell.getElement().getAttribute("j-test-id");
            if (headerCellJTEstId != null && !headerCellJTEstId.isEmpty()) {
                inputElement.setAttribute("j-test-id", "aggregationField");
            }
        }

        container.appendChild(inputElement);

        if (inputsList == null) {
            inputsList = new ArrayList<>();
        }
        inputsList.add(new AggregationInputFieldInfo(text, tableWidget.getColKeyByIndex(colIndex), inputElement));

        DOM.sinkEvents(inputElement, Event.ONCHANGE | Event.ONKEYDOWN);

        td.setClassName(tableWidget.getStylePrimaryName() + "-cell-content");
        td.addClassName(tableWidget.getStylePrimaryName() + "-aggregation-cell");
        td.appendChild(container);
        tr.appendChild(td);
    }

    protected void addCell(String text, char align, String style, boolean sorted) {
        final TableCellElement td = DOM.createTD().cast();

        final Element container = DOM.createDiv();
        container.setClassName(tableWidget.getStylePrimaryName() + "-cell-wrapper");

        td.setClassName(tableWidget.getStylePrimaryName() + "-cell-content");

        td.addClassName(tableWidget.getStylePrimaryName() + "-aggregation-cell");

        if (style != null && !style.equals("")) {
            td.addClassName(tableWidget.getStylePrimaryName() + "-cell-content-" + style);
        }

        if (sorted) {
            td.addClassName(tableWidget.getStylePrimaryName() + "-cell-content-sorted");
        }

        container.setInnerText(text);

        setAlign(align, container);

        td.appendChild(container);
        tr.appendChild(td);

        Tools.textSelectionEnable(td, tableWidget.isTextSelectionEnabled());
    }

    protected void setAlign(char align, final Element container) {
        // CAUTION: copied from VScrollTableRow
        switch (align) {
            case VScrollTable.ALIGN_CENTER:
                container.getStyle().setProperty("textAlign", "center");
                break;
            case VScrollTable.ALIGN_LEFT:
                container.getStyle().setProperty("textAlign", "left");
                break;
            case VScrollTable.ALIGN_RIGHT:
            default:
                container.getStyle().setProperty("textAlign", "right");
                break;
        }
    }

    protected AggregationInputFieldInfo getAggregationInputInfo(Element input) {
        if (inputsList == null) {
            return null;
        }

        for (AggregationInputFieldInfo info : inputsList) {
            if (info.getInputElement().isOrHasChild(input)) {
                return info;
            }
        }
        return null;
    }
}
