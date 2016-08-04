/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
package com.oracle.truffle.api.debug.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

import com.oracle.truffle.api.debug.DebugStackFrame;
import com.oracle.truffle.api.debug.DebugValue;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.source.Source;

public class DebugStackFrameTest extends AbstractDebugTest {

    @Test
    public void testEvalAndSideEffects() throws Throwable {
        final Source source = testSource("ROOT(DEFINE(a,ROOT( \n" +
                        "  VARIABLE(a, 42), \n" +
                        "  VARIABLE(b, 43), \n" +
                        "  VARIABLE(c, 44), \n" +
                        "  STATEMENT(),\n" + // will start stepping here
                        "  STATEMENT())\n" +
                        "), \n" +
                        "VARIABLE(a, 42), VARIABLE(b, 43), VARIABLE(c, 44), \n" +
                        "CALL(a))\n");
        try (DebuggerSession session = startSession()) {
            session.suspendNextExecution();
            startEval(source);

            SuspendedEvent event = expectSuspended();
            Iterator<DebugStackFrame> stackFrames = event.getStackFrames().iterator();
            // assert changes to the current frame
            DebugStackFrame frame = stackFrames.next();
            assertDynamicFrame(frame);
            DebugValue aValue = frame.getValue("a");
            String aStringValue = aValue.as(String.class);

            // assert changes to a parent frame
            frame = stackFrames.next();
            assertDynamicFrame(frame);

            // assign from one stack frame to another one
            frame.getValue("a").set(aValue);
            assertEquals(aStringValue, frame.getValue("a").as(String.class));
        }
    }

    private static void assertDynamicFrame(DebugStackFrame frame) {
        assertEquals("42", frame.getValue("a").as(String.class));
        assertEquals("43", frame.getValue("b").as(String.class));
        assertEquals("44", frame.getValue("c").as(String.class));

        // dynamic value should now be accessible
        DebugValue dStackValue = frame.getValue("d");
        assertNull(dStackValue);

        // should change the dynamic value
        assertEquals("45", frame.eval("VARIABLE(d, 45)").as(String.class));
        dStackValue = frame.getValue("d");
        assertEquals("45", dStackValue.as(String.class));
        assertEquals("45", frame.getValue("d").as(String.class));

        // change an existing value
        assertEquals("45", frame.eval("VARIABLE(c, 45)").as(String.class));
        assertEquals("45", frame.getValue("c").as(String.class));

        // set an existing value using a constant expression
        DebugValue bValue = frame.getValue("b");
        frame.getValue("b").set(frame.eval("CONSTANT(46)"));
        assertEquals("46", frame.getValue("b").as(String.class));
        assertEquals("46", bValue.as(String.class));

        // set an existing value using a constant expression with side effect
        frame.getValue("b").set(frame.eval("VARIABLE(a, 47)"));
        assertEquals("47", frame.getValue("b").as(String.class));
        assertEquals("47", frame.getValue("a").as(String.class));
    }

    @Test
    public void testFrameValidity() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  VARIABLE(a, 42), \n" +
                        "  VARIABLE(b, 43), \n" +
                        "  VARIABLE(c, 44), \n" +
                        "  STATEMENT(),\n" +
                        "  STATEMENT()\n" +
                        ")\n");
        try (DebuggerSession session = startSession()) {
            session.suspendNextExecution();
            startEval(source);

            SuspendedEvent event = expectSuspended();
            DebugStackFrame frame = event.getTopStackFrame();

            Iterator<DebugStackFrame> frameIterator = event.getStackFrames().iterator();
            assertSame(frame, frameIterator.next());
            assertFalse(frameIterator.hasNext());
            checkStack(frame, "a", "42", "b", "43", "c", "44");

            // values for verifying state checks
            Iterator<DebugStackFrame> frameIterator2 = event.getStackFrames().iterator();
            DebugValue stackValueWithGetValue = frame.getValue("a");
            DebugValue stackValueWithIterator = frame.iterator().next();

            // should dynamically create a local variable
            DebugValue heapValue = frame.eval("VARIABLE(d, 45)");
            event.prepareStepInto(1); // should render all pointers invalid

            // next event everything should be invalidated except heap values
            event = expectSuspended();

            assertInvalidFrame(frame);
            assertInvalidIterator(frameIterator2);
            assertInvalidDebugValue(stackValueWithGetValue);
            assertInvalidDebugValue(stackValueWithIterator);

            assertEquals("45", heapValue.as(String.class));
            assertFalse(heapValue.isWriteable());
            assertTrue(heapValue.isReadable());

            try {
                heapValue.set(heapValue);
                fail();
            } catch (IllegalStateException e) {
            }

            expectDone();
        }
    }

    private static void assertInvalidDebugValue(DebugValue value) {
        try {
            value.as(String.class);
            fail();
        } catch (IllegalStateException s) {
        }
        try {
            value.set(value);
            fail();
        } catch (IllegalStateException s) {
        }

        // should still work
        value.isReadable();
        value.isWriteable();
        value.getName();

    }

    private static void assertInvalidIterator(Iterator<DebugStackFrame> iterator) {
        try {
            iterator.hasNext();
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            iterator.next();
            fail();
        } catch (IllegalStateException s) {
        }
    }

    private static void assertInvalidFrame(DebugStackFrame frame) {
        try {
            frame.eval("STATEMENT");
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.getName();
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.getSourceSection();
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.getSourceSections();
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.getValue("d");
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.isInternal();
            fail();
        } catch (IllegalStateException s) {
        }

        try {
            frame.iterator();
            fail();
        } catch (IllegalStateException s) {
        }
    }

}
