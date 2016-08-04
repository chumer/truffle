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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class BreakpointTest extends AbstractDebugTest {

    @Test
    public void testBreakpointDefaults() {
        Source testSource = testSource("STATEMENT");
        Breakpoint breakpoint = Breakpoint.newBuilder(testSource).lineIs(1).build();
        assertNull(breakpoint.getConditionExpression());
        assertEquals(0, breakpoint.getHitCount());
        assertEquals(0, breakpoint.getIgnoreCount());
        assertFalse(breakpoint.isDisposed());
        assertTrue(breakpoint.isEnabled());
        assertFalse(breakpoint.isResolved());

        // Make some state changes
        breakpoint.setIgnoreCount(9);
        assertEquals(9, breakpoint.getIgnoreCount());

        breakpoint.setEnabled(false);
        assertFalse(breakpoint.isEnabled());

        breakpoint.setConditionExpression("a + b");
        assertEquals("a + b", breakpoint.getConditionExpression());

        breakpoint.dispose();

        assertTrue(breakpoint.isDisposed());
        assertFalse(breakpoint.isEnabled());
        assertFalse(breakpoint.isResolved());
    }

    @Test
    public void testBreakpointResolve() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        Breakpoint breakpoint2 = Breakpoint.newBuilder(testSource).lineIs(2).build();
        assertFalse(breakpoint2.isResolved());
        Breakpoint breakpoint3 = Breakpoint.newBuilder(testSource).lineIs(3).build();
        assertFalse(breakpoint3.isResolved());
        try (DebuggerSession session = startSession()) {
            session.install(breakpoint2);
            assertFalse(breakpoint2.isResolved());
            assertFalse(breakpoint3.isResolved());

            startEval(testSource);
            SuspendedEvent event = expectSuspended();
            assertSame(breakpoint2, event.getBreakpoints().iterator().next());
            assertTrue(breakpoint2.isResolved());
            expectDone();

            assertTrue(breakpoint2.isResolved());
            assertFalse(breakpoint3.isResolved());

            // breakpoint3 should be resolved by just installing it
            session.install(breakpoint3);
            assertTrue(breakpoint2.isResolved());
            assertTrue(breakpoint3.isResolved());
        }
    }

    @Test
    public void testBreakpointCondition() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            Breakpoint breakpoint = session.install(Breakpoint.newBuilder(testSource).lineIs(2).build());
            breakpoint.setConditionExpression("CONSTANT(true)");

            startEval(testSource);
            SuspendedEvent event = expectSuspended();
            assertSame(breakpoint, event.getBreakpoints().iterator().next());
            assertNull(event.getBreakpointConditionException(breakpoint));
            assertEquals(1, breakpoint.getHitCount());
            expectDone();

            breakpoint.setConditionExpression("CONSTANT(false)");
            startEval(testSource);
            assertEquals(1, breakpoint.getHitCount());
            expectDone();

            breakpoint.setConditionExpression("CONSTANT("); // error by parse exception
            startEval(testSource);
            event = expectSuspended();
            assertSame(breakpoint, event.getBreakpoints().iterator().next());
            assertNotNull(event.getBreakpointConditionException(breakpoint));
            assertEquals(2, breakpoint.getHitCount());
            expectDone();
        }
    }

    @Test
    public void testBreakURI1() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" + // break here
                        "  STATEMENT,\n" +
                        "  STATEMENT\n" +
                        ")\n");
        Breakpoint breakpoint = null;
        try (DebuggerSession session = startSession()) {
            breakpoint = session.install(Breakpoint.newBuilder(source.getURI()).lineIs(4).build());
            startEval(source);
            SuspendedEvent event = expectSuspended();
            checkState(event, 4, true, "STATEMENT");
            Assert.assertEquals(1, event.getBreakpoints().size());
            Assert.assertSame(breakpoint, event.getBreakpoints().get(0));
            Assert.assertEquals(1, breakpoint.getHitCount());
            Assert.assertEquals(true, breakpoint.isEnabled());
            expectDone();
        }
        Assert.assertEquals(false, breakpoint.isEnabled());
    }

    @Test
    public void testBreakURI2() throws Throwable {
        File testFile = testFile("ROOT(\n" +
                        "  DEFINE(foo,\n" +
                        "    LOOP(3,\n" +
                        "      STATEMENT)\n" +
                        "  ),\n" +
                        "  CALL(foo)\n" +
                        ")\n");

        try (DebuggerSession session = startSession()) {
            Breakpoint breakpoint = session.install(Breakpoint.newBuilder(testFile.toURI()).lineIs(4).build());
            session.suspendNextExecution();
            startEval(Source.newBuilder(testFile).build());
            for (int i = 0; i < 3; i++) {
                checkState(expectSuspended(), 4, true, "STATEMENT").prepareContinue();
            }
            Assert.assertEquals(3, breakpoint.getHitCount());
            expectDone();
        }
    }

    @Test
    public void testDisableBreakpointsDuringSuspend() throws Throwable {
        Source source = testSource("ROOT(\n" +
                        "  DEFINE(foo,\n" +
                        "    LOOP(3,\n" +
                        "      STATEMENT)\n" +
                        "  ),\n" +
                        "  CALL(foo)\n" +
                        ")\n");

        try (DebuggerSession session = startSession()) {
            Breakpoint breakpoint1 = session.install(Breakpoint.newBuilder(source).lineIs(4).build());
            Breakpoint breakpoint2 = session.install(Breakpoint.newBuilder(source).lineIs(4).build());
            Breakpoint breakpoint3 = session.install(Breakpoint.newBuilder(source).lineIs(4).build());
// session.suspendNextExecution();
            startEval(source);
            for (int i = 0; i < 3; i++) {
                checkState(expectSuspended(), 4, true, "STATEMENT").prepareContinue();
                if (i == 0) {
                    breakpoint3.dispose();
                }
                if (i == 1) {
                    breakpoint1.dispose();
                }
            }
            Assert.assertEquals(2, breakpoint1.getHitCount());
            Assert.assertEquals(3, breakpoint2.getHitCount());
            Assert.assertEquals(1, breakpoint3.getHitCount());

            expectDone();
        }
    }

    @Test
    public void testBreakSource() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" + // break here
                        "  STATEMENT,\n" +
                        "  STATEMENT\n" +
                        ")\n");
        Breakpoint breakpoint = null;
        try (DebuggerSession session = startSession()) {
            breakpoint = session.install(Breakpoint.newBuilder(source).lineIs(4).build());
            startEval(source);
            SuspendedEvent event = expectSuspended();
            checkState(event, 4, true, "STATEMENT");
            Assert.assertEquals(1, event.getBreakpoints().size());
            Assert.assertSame(breakpoint, event.getBreakpoints().get(0));
            Assert.assertEquals(1, breakpoint.getHitCount());
            Assert.assertEquals(true, breakpoint.isEnabled());
            expectDone();
        }
        Assert.assertEquals(false, breakpoint.isEnabled());
    }

    @Test
    public void testChangeDuringSuspension() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  DEFINE(foo,\n" +
                        "    STATEMENT\n" +
                        "  ),\n" +
                        "  STATEMENT,\n" +
                        "  CALL(foo)\n" +
                        ")\n");
        try (DebuggerSession session = startSession()) {
            session.suspendNextExecution();
            startEval(source);
            SuspendedEvent event = expectSuspended();
            checkState(event, 5, true, "STATEMENT");
            Assert.assertEquals(0, event.getBreakpoints().size());
            session.install(Breakpoint.newBuilder(source).lineIs(3).build());
            event.prepareContinue();

            event = expectSuspended();
            checkState(event, 3, true, "STATEMENT");
            event.prepareContinue();

            expectDone();
        }
    }

    @Test
    public void testOneShot() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  STATEMENT,\n" +
                        "  LOOP(3, STATEMENT),\n" +
                        "  STATEMENT\n" +
                        ")\n");
        try (DebuggerSession session = startSession()) {
            Breakpoint breakpoint = session.install(Breakpoint.newBuilder(source).lineIs(3).oneShot().build());

            startEval(source);

            SuspendedEvent event = expectSuspended();
            checkState(event, 3, true, "STATEMENT");
            Assert.assertEquals(1, event.getBreakpoints().size());
            Assert.assertSame(breakpoint, event.getBreakpoints().iterator().next());
            Assert.assertFalse(breakpoint.isEnabled());
            Assert.assertEquals(1, breakpoint.getHitCount());

            breakpoint.setEnabled(true); // reenable breakpoint to hit again
            event.prepareContinue();

            event = expectSuspended();
            Assert.assertEquals(1, event.getBreakpoints().size());
            Assert.assertSame(breakpoint, event.getBreakpoints().iterator().next());
            Assert.assertFalse(breakpoint.isEnabled());
            Assert.assertEquals(2, breakpoint.getHitCount());
            event.prepareContinue();

            // we don't reenable the breakpoint so we should not hit it again
            expectDone();
        }
    }

    @Test
    public void testBreakSourceSection() throws Throwable {
        final Source source = testSource("ROOT(STATEMENT, STATEMENT, STATEMENT)\n");
        try (DebuggerSession session = startSession()) {
            SourceSection sourceSection = source.createSection(null, 16, 9);
            Breakpoint breakpoint = session.install(Breakpoint.newBuilder(sourceSection).build());

            startEval(source);

            SuspendedEvent event = checkState(expectSuspended(), 1, true, "STATEMENT");
            Assert.assertEquals(sourceSection, event.getSourceSection());
            assertSame(breakpoint, event.getBreakpoints().iterator().next());
            event.prepareContinue();

            expectDone();
        }
    }

    @Test
    public void testDisableDispose() throws Throwable {
        final Source source = testSource("ROOT(\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT,\n" +
                        "  STATEMENT\n" +
                        ")\n");

        try (DebuggerSession session = startSession()) {
            // test normal breakpoint should hit
            Breakpoint breakpoint4 = session.install(Breakpoint.newBuilder(source).lineIs(4).build());

            // test disposed breakpoint should not hit
            Breakpoint breakpoint6 = session.install(Breakpoint.newBuilder(source).lineIs(6).build());
            breakpoint6.dispose();

            // test disabled breakpoint should not hit
            Breakpoint breakpoint8 = session.install(Breakpoint.newBuilder(source).lineIs(8).build());
            breakpoint8.setEnabled(false);

            // test re-enabled breakpoint should hit
            Breakpoint breakpoint10 = session.install(Breakpoint.newBuilder(source).lineIs(10).build());
            breakpoint10.setEnabled(false);
            breakpoint10.setEnabled(true);

            session.suspendNextExecution();
            startEval(source);

            checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
            checkState(expectSuspended(), 4, true, "STATEMENT").prepareContinue();
            checkState(expectSuspended(), 10, true, "STATEMENT").prepareContinue();

            expectDone();

            Assert.assertEquals(1, breakpoint4.getHitCount());
            Assert.assertTrue(breakpoint4.isEnabled());
            Assert.assertEquals(0, breakpoint6.getHitCount());
            Assert.assertFalse(breakpoint6.isEnabled());
            Assert.assertEquals(0, breakpoint8.getHitCount());
            Assert.assertFalse(breakpoint8.isEnabled());
            Assert.assertEquals(1, breakpoint10.getHitCount());
            Assert.assertTrue(breakpoint10.isEnabled());
        }
    }

}
