/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.object.basic.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.Layout.ImplicitCast;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.ObjectType;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.object.TypedLocation;
import com.oracle.truffle.object.Locations.DualLocation;
import com.oracle.truffle.object.basic.DefaultLayoutFactory;

@RunWith(Parameterized.class)
public class ImplicitCastTest {

    static final Layout longLayout = new DefaultLayoutFactory().createLayout(Layout.newLayout().addAllowedImplicitCast(ImplicitCast.IntToLong));
    static final Layout doubleLayout = new DefaultLayoutFactory().createLayout(Layout.newLayout().addAllowedImplicitCast(ImplicitCast.IntToDouble));

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                        {longLayout, 1, 1L << 42, long.class},
                        {doubleLayout, 1, 3.14, double.class}
        });
    }

    final Layout layout;
    final int intVal;
    final Object otherVal;
    final Class<?> otherPrimClass;

    public ImplicitCastTest(Layout layout, int intVal, Object otherVal, Class<?> otherPrimClass) {
        this.layout = layout;
        this.intVal = intVal;
        this.otherVal = otherVal;
        this.otherPrimClass = otherPrimClass;
    }

    @Test
    public void testIntOther() {
        Shape rootShape = layout.createShape(new ObjectType());
        DynamicObject object = rootShape.newInstance();
        object.define("a", intVal);
        Location location = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location instanceof DualLocation);
        Assert.assertEquals(int.class, ((TypedLocation) location).getType());

        object.define("a", otherVal);
        Location location2 = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location2 instanceof DualLocation);
        Assert.assertEquals(otherPrimClass, ((TypedLocation) location2).getType());
        Assert.assertEquals(otherVal.getClass(), object.get("a").getClass());
    }

    @Test
    public void testOtherInt() {
        Shape rootShape = layout.createShape(new ObjectType());
        DynamicObject object = rootShape.newInstance();
        object.define("a", otherVal);
        Location location = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location instanceof DualLocation);
        Assert.assertEquals(otherPrimClass, ((TypedLocation) location).getType());

        object.define("a", intVal);
        Location location2 = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location2 instanceof DualLocation);
        Assert.assertEquals(otherPrimClass, ((TypedLocation) location2).getType());
        Assert.assertEquals(otherVal.getClass(), object.get("a").getClass());
    }

    @Test
    public void testIntObject() {
        Shape rootShape = layout.createShape(new ObjectType());
        DynamicObject object = rootShape.newInstance();
        object.define("a", intVal);
        object.define("a", "");
        Location location = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location instanceof DualLocation);
        Assert.assertEquals(Object.class, ((TypedLocation) location).getType());
        Assert.assertEquals(String.class, object.get("a").getClass());
    }

    @Test
    public void testIntOtherObject() {
        Shape rootShape = layout.createShape(new ObjectType());
        DynamicObject object = rootShape.newInstance();
        object.define("a", intVal);
        object.define("a", otherVal);
        object.define("a", "");
        Location location = object.getShape().getProperty("a").getLocation();
        Assert.assertTrue(location instanceof DualLocation);
        Assert.assertEquals(Object.class, ((TypedLocation) location).getType());
        Assert.assertEquals(String.class, object.get("a").getClass());
    }

}
