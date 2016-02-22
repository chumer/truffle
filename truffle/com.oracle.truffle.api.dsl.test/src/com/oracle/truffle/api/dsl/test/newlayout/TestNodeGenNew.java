package com.oracle.truffle.api.dsl.test.newlayout;

import static com.oracle.truffle.api.dsl.test.newlayout.Sharable.asImplicitDouble;
import static com.oracle.truffle.api.dsl.test.newlayout.Sharable.isImplicitDouble;
import static com.oracle.truffle.api.dsl.test.newlayout.Sharable.specializeImplicitDouble;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.test.examples.ExampleNode;
import com.oracle.truffle.api.dsl.test.examples.ExampleTypesGen;
import com.oracle.truffle.api.dsl.test.newlayout.TestNodeBenchmark.IntNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@GeneratedBy(TestNode.class)
public final class TestNodeGenNew extends TestNode {

    private static final int MASK_UNINITIALIZED = 0b0_0000_0000_1;
    private static final int MASK_DO_INT_CACHED = 0b0_0000_0111_0;
    private static final int MASK_DO_INT_CACHED0 = 0b0_0000_0001_0;
    private static final int MASK_DO_INT_CACHED1 = 0b0_0000_0010_0;
    private static final int MASK_DO_INT_CACHED2 = 0b0_0000_0100_0;
    private static final int MASK_DO_INT_OVERFLOW = 0b0_0000_1000_0;
    private static final int MASK_DO_INT_NO_OVERFLOW = 0b0_0001_0000_0;
    private static final int MASK_INT = MASK_DO_INT_CACHED | MASK_DO_INT_OVERFLOW | MASK_DO_INT_NO_OVERFLOW;
    private static final int MASK_DO_DOUBLE = 0b11110_0000_0;
    private static final int MASK_DO_FALLBACK = 0b100000_0000_0;
    private static final int ARG0_ALWAYS_INT = MASK_INT | 0b01110_0000_0;
    private static final int ARG1_ALWAYS_INT = MASK_INT | 0b11010_0000_0;
    private static final int ARG0_ALWAYS_DOUBLE = 0b10110_0000_0;
    private static final int ARG1_ALWAYS_DOUBLE = 0b11100_0000_0;

    @Child private ExampleNode args0_;
    @Child private ExampleNode args1_;

    @CompilationFinal private int state = MASK_UNINITIALIZED;
    @CompilationFinal private int exclude_ = MASK_UNINITIALIZED;

    @CompilationFinal private int cachedArg0Value0;
    @CompilationFinal private int cachedArg0Value1;
    @CompilationFinal private int cachedArg0Value2;

    @CompilationFinal private int cachedArg1Value0;
    @CompilationFinal private int cachedArg1Value1;
    @CompilationFinal private int cachedArg1Value2;

    private TestNodeGenNew(ExampleNode[] args) {
        this.args0_ = args != null && 0 < args.length ? args[0] : null;
        this.args1_ = args != null && 1 < args.length ? args[1] : null;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        int state_ = this.state;

        Object arg0ValueObject;
        try {
            arg0ValueObject = executeArg0(frame, state_);
        } catch (UnexpectedResultException e) {
            arg0ValueObject = e.getResult();
            Object arg1ValueObject = args1_.execute(frame);
            return specializeAndExecute(state_, frame, arg0ValueObject, arg1ValueObject);
        }
        Object arg1ValueObject;
        try {
            arg1ValueObject = executeArg1(frame, state_);
        } catch (UnexpectedResultException e) {
            arg1ValueObject = e.getResult();
            return specializeAndExecute(state_, frame, arg0ValueObject, arg1ValueObject);
        }
        return executeGeneric(state_, frame, arg0ValueObject, arg1ValueObject);
    }

    /*
     * Should be inlined and boxing eliminated by Graal. Is it?
     */
    private Object executeArg0(VirtualFrame frame, int state_) throws UnexpectedResultException {
        Object arg0ValueObject;
        if ((state_ & ~ARG0_ALWAYS_INT) == 0) { // always int
            arg0ValueObject = args0_.executeInt(frame);
        } else if ((state_ & ~ARG0_ALWAYS_DOUBLE) == 0) { // always double
            arg0ValueObject = args0_.executeDouble(frame);
        } else { // mixed
            arg0ValueObject = args0_.execute(frame);
        }
        return arg0ValueObject;
    }

    private Object executeArg1(VirtualFrame frame, int state_) throws UnexpectedResultException {
        Object arg1ValueObject = null;
        if ((state_ & ~ARG1_ALWAYS_INT) == 0) { // always int
            arg1ValueObject = args1_.executeInt(frame);
        } else if ((state_ & ~ARG1_ALWAYS_DOUBLE) == 0) { // always double
            arg1ValueObject = args1_.executeDouble(frame);
        } else { // mixed
            arg1ValueObject = args1_.execute(frame);
        }
        return arg1ValueObject;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        int state_ = this.state;
        int arg0Value_;
        try {
            arg0Value_ = args0_.executeInt(frame);
        } catch (UnexpectedResultException e) {
            return ExampleTypesGen.expectInteger(specializeAndExecute(state_, frame, e.getResult(), args1_.execute(frame)));
        }
        int arg1Value_;
        try {
            arg1Value_ = args1_.executeInt(frame);
        } catch (UnexpectedResultException e) {
            return ExampleTypesGen.expectInteger(specializeAndExecute(state_, frame, arg0Value_, e.getResult()));
        }

        if ((state_ & MASK_DO_INT_CACHED) != 0) {
            if ((state_ & MASK_DO_INT_CACHED0) != 0 && cachedArg0Value0 == arg0Value_ && cachedArg1Value0 == arg1Value_) {
                return doIntCached(arg0Value_, arg1Value_, cachedArg0Value0, cachedArg1Value0);
            }
            if ((state_ & MASK_DO_INT_CACHED1) != 0 && cachedArg0Value1 == arg0Value_ && cachedArg1Value1 == arg1Value_) {
                return doIntCached(arg0Value_, arg1Value_, cachedArg0Value1, cachedArg1Value1);
            }
            if ((state_ & MASK_DO_INT_CACHED2) != 0 && cachedArg0Value2 == arg0Value_ && cachedArg1Value2 == arg1Value_) {
                return doIntCached(arg0Value_, arg1Value_, cachedArg0Value2, cachedArg1Value2);
            }
        }

        if ((state_ & MASK_DO_INT_OVERFLOW) != 0) {
            try {
                return doIntRewrite(arg0Value_, arg1Value_);
            } catch (ArithmeticException e) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                exclude_ |= 0b1000;
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return ExampleTypesGen.expectInteger(specializeAndExecute(state_, frame, arg0Value_, arg1Value_));
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        final int state_ = this.state;

        Object arg0ValueObject;
        Object arg1ValueObject;

        try {
            arg0ValueObject = executeArg0(frame, state_);
        } catch (UnexpectedResultException e) {
            arg0ValueObject = e.getResult();
            arg1ValueObject = args1_.execute(frame);
            return ExampleTypesGen.expectDouble(specializeAndExecute(state_, frame, arg0ValueObject, arg1ValueObject));
        }

        try {
            arg1ValueObject = executeArg1(frame, state_);
        } catch (UnexpectedResultException e) {
            arg1ValueObject = e.getResult();
            return ExampleTypesGen.expectDouble(specializeAndExecute(state_, frame, arg0ValueObject, arg1ValueObject));
        }

        if ((state_ & MASK_DO_INT_NO_OVERFLOW) != 0 && arg0ValueObject instanceof Integer && arg1ValueObject instanceof Integer) {
            int arg0ValueInt = (int) arg0ValueObject;
            int arg1ValueInt = (int) arg1ValueObject;
            return doIntNoRewrite(arg0ValueInt, arg1ValueInt);
        }

        final int maskArg0 = state_ >>> 8 & 0x3;
        final int maskArg1 = state_ >>> 6 & 0x3;
        if ((state_ & MASK_DO_DOUBLE) != 0 && isImplicitDouble(maskArg0, arg0ValueObject) && isImplicitDouble(maskArg1, arg1ValueObject)) {
            double arg0ValueDouble = asImplicitDouble(maskArg0, arg0ValueObject);
            double arg1ValueDouble = asImplicitDouble(maskArg1, arg1ValueObject);
            return doDouble(arg0ValueDouble, arg1ValueDouble);
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        return ExampleTypesGen.expectDouble(specializeAndExecute(state_, frame, arg0ValueObject, arg1ValueObject));
    }

    @Override
    public NodeCost getCost() {
        return Sharable.calculateCost(state);
    }

    private Object executeGeneric(final int state_, VirtualFrame frameValue, final Object arg0ValueObject, final Object arg1ValueObject) {
        if ((state_ & MASK_INT) != 0 && arg0ValueObject instanceof Integer && arg1ValueObject instanceof Integer) {
            int arg0ValueInt = (int) arg0ValueObject;
            int arg1ValueInt = (int) arg1ValueObject;

            if ((state_ & MASK_DO_INT_CACHED) != 0) {
                if ((state_ & MASK_DO_INT_CACHED0) != 0 && cachedArg0Value0 == arg0ValueInt && cachedArg1Value0 == arg1ValueInt) {
                    return doIntCached(arg0ValueInt, arg1ValueInt, cachedArg0Value0, cachedArg1Value0);
                }
                if ((state_ & MASK_DO_INT_CACHED1) != 0 && cachedArg0Value1 == arg0ValueInt && cachedArg1Value1 == arg1ValueInt) {
                    return doIntCached(arg0ValueInt, arg1ValueInt, cachedArg0Value1, cachedArg1Value1);
                }
                if ((state_ & MASK_DO_INT_CACHED2) != 0 && cachedArg0Value2 == arg0ValueInt && cachedArg1Value2 == arg1ValueInt) {
                    return doIntCached(arg0ValueInt, arg1ValueInt, cachedArg0Value2, cachedArg1Value2);
                }
            }

            if ((state_ & MASK_DO_INT_OVERFLOW) != 0) {
                try {
                    return doIntRewrite(arg0ValueInt, arg1ValueInt);
                } catch (ArithmeticException e) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    synchronized (this) {
                        exclude_ |= MASK_DO_INT_OVERFLOW;
                    }
                }
            }

            if ((state_ & MASK_DO_INT_NO_OVERFLOW) != 0) {
                return doIntNoRewrite(arg0ValueInt, arg1ValueInt);
            }
        }

        final int maskArg0 = state_ >>> 8 & 0x3;
        final int maskArg1 = state_ >>> 6 & 0x3;
        if ((state_ & MASK_DO_DOUBLE) != 0 && isImplicitDouble(maskArg0, arg0ValueObject) && isImplicitDouble(maskArg1, arg1ValueObject)) {
            double arg0ValueDouble = asImplicitDouble(maskArg0, arg0ValueObject);
            double arg1ValueDouble = asImplicitDouble(maskArg1, arg1ValueObject);
            return doDouble(arg0ValueDouble, arg1ValueDouble);
        }

        if ((state_ & MASK_DO_FALLBACK) != 0) {
            // fallback
            if ((state_ | nextState(state_, arg0ValueObject, arg1ValueObject)) == state_) {
                return doFallback(arg0ValueObject, arg1ValueObject);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return specializeAndExecute(state_, frameValue, arg0ValueObject, arg1ValueObject);
    }

    private Object specializeAndExecute(int state_, VirtualFrame frameValue, Object args0Value, Object args1Value) {
        synchronized (this) {
            state = (state | nextState(state_, args0Value, args1Value)) & ~exclude_;
        }
        return executeGeneric(state, frameValue, args0Value, args1Value);
    }

    @TruffleBoundary
    private int nextState(int state_, Object args0Value, Object args1Value) {
        int exclude = exclude_;
        if (args0Value instanceof Integer && args1Value instanceof Integer) {
            int arg0Value_ = (int) args0Value;
            int arg1Value_ = (int) args1Value;
            if ((exclude & MASK_DO_INT_CACHED0) == 0 && (state_ & MASK_DO_INT_CACHED0) == 0 ||
                            (cachedArg0Value0 == arg0Value_ && cachedArg1Value0 == arg1Value_)) {
                cachedArg0Value0 = arg0Value_;
                cachedArg1Value0 = arg1Value_;
                return MASK_DO_INT_CACHED0; // doIntCached_0
            }
            if ((exclude & MASK_DO_INT_CACHED1) == 0 && (state_ & MASK_DO_INT_CACHED1) == 0 ||
                            (cachedArg0Value1 == arg0Value_ && cachedArg1Value1 == arg1Value_)) {
                cachedArg0Value1 = arg0Value_;
                cachedArg1Value1 = arg1Value_;
                return MASK_DO_INT_CACHED1; // doIntCached_1
            }
            if ((exclude & MASK_DO_INT_CACHED2) == 0 && (state_ & MASK_DO_INT_CACHED2) == 0 ||
                            (cachedArg0Value2 == arg0Value_ && cachedArg1Value2 == arg1Value_)) {
                cachedArg0Value2 = arg0Value_;
                cachedArg1Value2 = arg1Value_;
                return MASK_DO_INT_CACHED2; // doIntCached_2
            }

            if ((exclude & MASK_DO_INT_OVERFLOW) == 0) {
                exclude_ |= MASK_DO_INT_CACHED; // doIntRewrite contains doIntCached
                return MASK_DO_INT_OVERFLOW; // doIntRewrite
            }
            return MASK_DO_INT_NO_OVERFLOW; // doIntNoRewrite
        }

        int args0ImplicitDoubleCast;
        int args1ImplicitDoubleCast;
        if ((exclude & 0b10_0000) == 0 &&
                        (args0ImplicitDoubleCast = specializeImplicitDouble(args0Value)) != 0 &&
                        (args1ImplicitDoubleCast = specializeImplicitDouble(args1Value)) != 0) {
            return args0ImplicitDoubleCast << 8 | args1ImplicitDoubleCast << 6; // doDouble
        }

        return MASK_DO_FALLBACK; // fallback
    }

    public static TestNode create(ExampleNode[] args) {
        return new TestNodeGenNew(args);
    }

    public static void main(String[] args) throws UnexpectedResultException {
        TestNodeGenNew node = new TestNodeGenNew(new ExampleNode[]{new IntNode(2), new IntNode(3)});

        for (int i = 0; i < 1000; i++) {
            node.executeInt(null);
        }
        node.executeInt(null);

    }

}
