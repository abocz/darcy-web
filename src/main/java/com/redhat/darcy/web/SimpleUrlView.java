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

import com.redhat.darcy.ui.api.ElementContext;
import com.redhat.darcy.ui.api.View;
import com.redhat.darcy.ui.internal.Initializer;
import com.redhat.darcy.util.ReflectionUtil;
import com.redhat.darcy.web.api.Browser;
import com.redhat.darcy.web.api.ViewUrl;

import java.lang.reflect.Field;
import java.util.List;

/**
 * An implementation of View used to represent pages where the only load condition is the URL.
 *
 * SimpleUrlView initializes element and view fields but does not verify if they are available. It treats all
 * Element fields as if they had @NotRequired annotation and ignores @Requires and @RequiresAll annotations.
 */
public class SimpleUrlView implements View, ViewUrl<SimpleUrlView> {

    private final String url;
    private final Initializer initializer;

    private ElementContext context;

    public SimpleUrlView(String url) {
        this.url = url;

        List<Field> declaredFields = ReflectionUtil.getAllDeclaredFields(this);
        initializer = new Initializer(this, declaredFields);
    }

    @Override
    public void setContext(ElementContext context) {
        this.context = context;
        initializer.initializeFields(context);

        onSetContext();
    }

    /**
     * Called after Context is set on the View. Can be overriden by subclasses to perform custom intialization.
     */
    protected void onSetContext() {}

    @Override
    public ElementContext getContext() {
        return context;
    }

    /**
     * Determines if the view is loaded by checking the browser's URL
     * @return
     */
    @Override
    public boolean isLoaded() {
        return url.equals(((Browser) context).getCurrentUrl());
    }

    /**
     * the view's URL
     * @return
     */
    @Override
    public String url() {
        return url;
    }

    /**
     * the view itself
     * @return
     */
    @Override
    public SimpleUrlView destination() {
        return this;
    }
}
