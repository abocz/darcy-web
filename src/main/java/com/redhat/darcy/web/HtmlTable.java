/*
 Copyright 2014 Red Hat, Inc. and/or its affiliates.

 This file is part of darcy-web.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.darcy.web;

import static com.redhat.darcy.web.HtmlElements.htmlElement;

import com.redhat.darcy.ui.AbstractViewElement;
import com.redhat.darcy.ui.api.Locator;
import com.redhat.darcy.ui.api.elements.Element;
import com.redhat.darcy.ui.api.elements.Table;
import com.redhat.darcy.ui.internal.ViewList;
import com.redhat.darcy.web.api.WebContext;
import com.redhat.darcy.web.api.elements.HtmlElement;
import com.redhat.darcy.web.api.elements.HtmlLink;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class HtmlTable extends AbstractViewElement implements Table<HtmlTable>, HtmlElement {
    private final HtmlElement bodyTag = htmlElement(byInner(By.htmlTag("tbody")));

    public HtmlTable(Locator parent) { super(parent); }
    public HtmlTable(Element parent) { super(parent); }

    public static HtmlTable htmlTable(Locator parent) {
        return new HtmlTable(parent);
    }

    public static List<HtmlTable> htmlTables(Locator parents) {
        return new ViewList<>(HtmlTable::new, parents);
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed();
    }

    @Override
    public boolean isDisplayed() {
        return parent.isDisplayed();
    }

    @Override
    public boolean isPresent() {
        return parent.isPresent();
    }

    @Override
    public int getRowCount() {
        String xpath = bodyTag.isPresent()
            ? "./tbody/tr"
            : "./tr";

        return getContext().find().elements(byInner(By.xpath(xpath))).size();
    }

    @Override
    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    @Override
    public String getTagName() {
        return ((HtmlElement) parent).getTagName();
    }

    @Override
    public String getCssValue(String property) {
        return ((HtmlElement) parent).getCssValue(property);
    }

    @Override
    public Set<String> getClasses() {
        return ((HtmlElement) parent).getClasses();
    }

    @Override
    public String getAttribute(String attribute) {
        return ((HtmlElement) parent).getAttribute(attribute);
    }

    protected Locator byRowColumn(int row, int col) {
        if (row < 1) {
            throw new IllegalArgumentException("Row index must be greater than 0.");
        }

        if (col < 1) {
            throw new IllegalArgumentException("Column index must be greater than 0.");
        }

        String xpath = bodyTag.isPresent()
                ? "./tbody/tr[" + row + "]/td[" + col + "]"
                : "./tr[" + row + "]/td[" + col + "]";

        return byInner(By.xpath(xpath));
    }

    /**
     * A partial {@link com.redhat.darcy.ui.api.elements.Table.ColumnDefinition} implementation for
     * {@link com.redhat.darcy.web.HtmlTable HtmlTables}.
     * @param <T> The type of contents within this column.
     */
    public static class HtmlColumn<T> implements ColumnDefinition<HtmlTable, T> {
        private final BiFunction<WebContext, Locator, T> cellDefinition;
        private final int index;

        /**
         * Creates a new column definition for a column within an
         * {@link com.redhat.darcy.web.HtmlTable}.
         * @param cellDefinition A function that takes the context of the table, and a locator to
         * an element within that column. Together, they can be used to find a cell within that
         * column, or possibly an element nested within that cell. The function is expected to
         * return the content of that cell, typed appropriately by T.
         * @param index The index of the column, counting from 1, where 1 is the leftmost column.
         */
        public HtmlColumn(BiFunction<WebContext, Locator, T> cellDefinition, int index) {
            this.cellDefinition = cellDefinition;
            this.index = index;
        }

        @Override
        public T getCell(HtmlTable table, int row) {
            return cellDefinition.apply((WebContext) table.getContext(), table.byRowColumn(row, index));
        }
    }

    public static class HtmlTextColumn extends HtmlColumn<String> {
        public HtmlTextColumn(int index) {
            super((c, l) -> c.find().text(l).getText(), index);
        }
    }

    public static class HtmlLinkColumn extends HtmlColumn<HtmlLink> {
        public HtmlLinkColumn(int index) {
            super((c, l) -> c.find().htmlLink(By.chained(l, By.xpath("./a"))), index);
        }
    }
}