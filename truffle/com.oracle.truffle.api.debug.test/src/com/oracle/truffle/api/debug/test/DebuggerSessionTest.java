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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendedCallback;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;

public class DebuggerSessionTest extends AbstractDebugTest {

    @Test
    public void testSuspendNextExecution1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspendNextExecution();
            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
            expectDone();
        }
    }

    @Test
    public void testSuspendNextExecution2() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {

            // calling it multiple times should not make a difference
            session.suspendNextExecution();
            session.suspendNextExecution();
            session.suspendNextExecution();

            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
            expectDone();
        }
    }

    @Test
    public void testSuspendNextExecution3() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {

            // do suspend next for a few times
            for (int i = 0; i < 100; i++) {
                session.suspendNextExecution();

                startEval(testSource);
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
                expectDone();
            }
        }
    }

    @Test
    public void testSuspendNextExecution4() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspendNextExecution();

            startEval(testSource);
            SuspendedEvent event = expectSuspended();
            checkState(event, 2, true, "STATEMENT").prepareContinue();

            // use suspend next in an event
            event.getSession().suspendNextExecution();

            checkState(expectSuspended(), 3, true, "STATEMENT").prepareContinue();

            expectDone();
        }
    }

    @Test
    public void testSuspendThread1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            // do suspend next for a few times
            for (int i = 0; i < 100; i++) {
                session.suspend(getEvalThread());
                startEval(testSource);
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
                expectDone();
            }
        }
    }

    @Test
    public void testSuspendThread2() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspend(getEvalThread());
            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();

            // prepareContinue should be ignored here as suspenions counts higher
            session.suspend(getEvalThread());

            checkState(expectSuspended(), 3, true, "STATEMENT").prepareContinue();

            expectDone();
        }
    }

    @Test
    public void testSuspendThread3() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspend(getEvalThread());
            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareKill();

            // For prepareKill additional suspensions should be ignored
            session.suspend(getEvalThread());

            expectKilled();
        }
    }

    @Test
    public void testSuspendAll1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendAll();
                startEval(testSource);
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();
                expectDone();
            }
        }
    }

    @Test
    public void testSuspendAll2() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspendAll();
            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareContinue();

            // prepareContinue should be ignored here as suspenions counts higher
            session.suspendAll();

            checkState(expectSuspended(), 3, true, "STATEMENT").prepareContinue();

            expectDone();
        }
    }

    @Test
    public void testSuspendAll3() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            session.suspendAll();
            startEval(testSource);
            checkState(expectSuspended(), 2, true, "STATEMENT").prepareKill();

            // For prepareKill additional suspensions should be ignored
            session.suspendAll();

            expectKilled();
        }
    }

    @Test
    public void testResumeThread1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendNextExecution();
                startEval(testSource);
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareStepOver(1);
                // resume events are ignored by stepping
                session.resume(getEvalThread());
                expectDone();
            }
        }
    }

    @Test
    public void testResumeThread2() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendNextExecution();
                session.resume(getEvalThread());
                startEval(testSource);

                // even if the thread is resumed suspend next execution will trigger.
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareStepOver(1);
                session.resume(getEvalThread());
                expectDone();
            }
        }
    }

    @Test
    public void testResumeAll1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendNextExecution();
                session.resumeAll(); // resume all invalidates suspendNextExecution
                startEval(testSource);
                expectDone();
            }
        }
    }

    @Test
    public void testResumeAll2() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendAll();
                session.resumeAll();
                startEval(testSource);
                expectDone();
            }
        }
    }

    @Test
    public void testResumeAll3() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspend(getEvalThread());
                session.resumeAll();
                startEval(testSource);
                expectDone();
            }
        }
    }

    @Test
    public void testResumeAll4() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        try (DebuggerSession session = startSession()) {
            for (int i = 0; i < 10; i++) {
                session.suspendNextExecution();
                startEval(testSource);
                checkState(expectSuspended(), 2, true, "STATEMENT").prepareStepOver(1);
                session.resumeAll(); // test that resume does not affect current stepping behavior
                checkState(expectSuspended(), 3, true, "STATEMENT").prepareStepOver(1);
                expectDone();
            }
        }
    }

    @Test
    public void testClosing1() {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        DebuggerSession session = startSession();

        session.install(Breakpoint.newBuilder(testSource).lineIs(3).build());
        session.suspendNextExecution();
        startEval(testSource);
        checkState(expectSuspended(), 2, true, "STATEMENT").prepareStepOver(1);

        // closing the session should disable breakpoints and current stepping
        session.close();

        expectDone();
    }

    @Test
    public void testClosing2() throws IOException {
        Source testSource = testSource("ROOT(\n" +
                        "STATEMENT,\n" +
                        "STATEMENT)");

        PolyglotEngine engine = PolyglotEngine.newBuilder().build();
        final AtomicBoolean suspend = new AtomicBoolean();
        DebuggerSession session = Debugger.find(engine).startSession(new SuspendedCallback() {
            public void onSuspend(SuspendedEvent event) {
                suspend.set(true);
            }
        });
        engine.eval(testSource);

        engine.dispose();

        // if the engine disposes the session should still work
        session.suspendNextExecution();
        session.suspend(Thread.currentThread());
        session.suspendAll();
        session.install(Breakpoint.newBuilder(testSource).lineIs(2).build());
        session.resume(Thread.currentThread());
        session.resumeAll();
        session.getDebugger();
        session.getBreakpoints();

        // after closing the session none of these methods should work
        session.close();

        try {
            session.suspendNextExecution();
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            session.suspend(Thread.currentThread());
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            session.suspendAll();
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            session.install(Breakpoint.newBuilder(testSource).lineIs(2).build());
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            session.resume(Thread.currentThread());
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            session.resumeAll();
            Assert.fail();
        } catch (IllegalStateException e) {
        }
        try {
            session.getBreakpoints();
            Assert.fail();
        } catch (IllegalStateException e) {
        }

        // still works after closing
        session.getDebugger();
    }

}
